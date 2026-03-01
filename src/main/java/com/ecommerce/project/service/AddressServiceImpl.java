package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Address;
import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.AddressDTO;
import com.ecommerce.project.repositories.AddressRepository;
import com.ecommerce.project.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService{

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO, User user) {
        Address address = modelMapper.map(addressDTO, Address.class);
        List<Address> addresses = user.getAddresses();
        addresses.add(address);
        user.setAddresses(addresses);
        Address savedAddress = addressRepository.save(address);
        userRepository.save(user);
        List<User> users = address.getUsers();
        users.add(user);
        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAllAddresses() {
        List<Address> addresses = addressRepository.findAll();
        return addresses.stream().map(address -> modelMapper.map(address, AddressDTO.class)).toList();
    }

    @Override
    public AddressDTO getAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId).orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));
        return modelMapper.map(address, AddressDTO.class);
    }

    @Override                                                  
    public List<AddressDTO> getUserAddresses(User user) {
        List<Address> addresses = user.getAddresses();
        return addresses.stream().map(address -> modelMapper.map(address, AddressDTO.class)).toList();
    }

    @Override
    public AddressDTO updateAddress(Long addressId, AddressDTO addressDTO) {
        Address addressFromDB = addressRepository.findById(addressId).orElseThrow(() -> new ResourceNotFoundException("Address", "addressID", addressId));
        addressFromDB.setCity(addressDTO.getCity());
        addressFromDB.setZipCode(addressDTO.getZipCode());
        addressFromDB.setState(addressDTO.getState());
        addressFromDB.setCountry(addressDTO.getCountry());
        addressFromDB.setStreet(addressDTO.getStreet());
        addressFromDB.setBuildingName(addressDTO.getBuildingName());
        Address updatedAddress = addressRepository.save(addressFromDB);

        List<User> users = addressFromDB.getUsers();
        users.forEach(user -> {
            user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
            user.getAddresses().add(updatedAddress);
            userRepository.save(user);
        });

        return modelMapper.map(updatedAddress, AddressDTO.class);
    }

    @Override
    public String deleteAddress(Long addressId) {
        Address targetAddress = addressRepository.findById(addressId).orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));
        List<User> users = targetAddress.getUsers();
        users.forEach(user -> {
            user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
            userRepository.save(user);
        });
        addressRepository.delete(targetAddress);
        return "Address is deleted successfully";
    }
}
