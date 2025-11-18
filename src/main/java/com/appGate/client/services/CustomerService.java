package com.appGate.client.services;

import com.appGate.client.dto.CustomerDto;
import com.appGate.client.dto.SuspendCustomerDto;
import com.appGate.client.dto.UnblockCustomerDto;
import com.appGate.client.enums.CustomerTypeEnum;
import com.appGate.client.models.Customer;
import com.appGate.client.repository.CustomerRepository;
import com.appGate.client.response.BaseResponse;
import com.appGate.client.util.FileUploadUtil;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public BaseResponse createWalkinCustomer(CustomerDto customerDto, HttpServletRequest request) {

        String baseUrl = getBaseUrl(request);

        return new BaseResponse(HttpStatus.CREATED.value(), "successful", saveCustomer(customerDto, baseUrl));
    }

    private String getBaseUrl(HttpServletRequest request) {
        String forwardedHost = request.getHeader("X-Forwarded-Host");
        String forwardedProto = request.getHeader("X-Forwarded-Proto");
        String forwardedPrefix = request.getHeader("X-Forwarded-Prefix");

        // Build the original URL
        StringBuilder originalUrl = new StringBuilder();

        // Protocol (http or https)
        if (forwardedProto != null) {
            originalUrl.append(forwardedProto).append("://");
        } else {
            originalUrl.append(request.getScheme()).append("://");
        }

        // Host and port
        if (forwardedHost != null) {
            originalUrl.append(forwardedHost);
        } else {
            originalUrl.append(request.getServerName());
            if (request.getServerPort() != 80 && request.getServerPort() != 443) {
                originalUrl.append(":").append(request.getServerPort());
            }
        }

        // Path prefix if any
        if (forwardedPrefix != null) {
            originalUrl.append(forwardedPrefix);
        }

        return originalUrl.toString();
    }

    private String saveImage(MultipartFile file, String uploadDir, String baseUrl) {

        String fileSavedPath = "";
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            fileSavedPath = FileUploadUtil.saveImage(uploadDir, FileUploadUtil.generateUniqueName(fileName), file);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "error", e);
        }

        fileSavedPath = baseUrl + "/api/users/customer/image/" + fileSavedPath;

        System.out.println("fileSavedPath " + fileSavedPath);
        System.out.println("baseUrl " + baseUrl);

        return fileSavedPath;
    }

    private Customer saveCustomer(CustomerDto customerDto, String baseUrl) {

        Customer customer = new Customer();
        customer.setAccountNumber(customerDto.getAccountNumber());
        customer.setEmail(customerDto.getEmail());
        customer.setSurname(customerDto.getSurname());
        customer.setFirstName(customerDto.getFirstName());
        customer.setDob(customerDto.getDob());
        customer.setPhoneNumber(customerDto.getPhoneNumber());
        customer.setOccupation(customerDto.getOccupation());
        customer.setNationality(customerDto.getNationality());
        customer.setNin(customerDto.getNin());
        customer.setBvn(customerDto.getBvn());
        customer.setContactAddress(customerDto.getContactAddress());
        customer.setOfficeAddress(customerDto.getOfficeAddress());
        customer.setNextOfKin(customerDto.getNextOfKin());
        customer.setNextOfKinAddress(customerDto.getNextOfKinAddress());
        customer.setPassport(saveImage(customerDto.getPassport(), "customer", baseUrl));
        customer.setSignature(saveImage(customerDto.getSignature(), "customer", baseUrl));
        customer.setGender(customerDto.getGender());
        customer.setCustomerType(CustomerTypeEnum.WALKIN);

        return customerRepository.save(customer);
    }

    private void updateCustomer(Customer customer, CustomerDto customerDto) {
        customer.setAccountNumber(customerDto.getAccountNumber());
        customer.setEmail(customerDto.getEmail());
        customer.setSurname(customerDto.getSurname());
        customer.setFirstName(customerDto.getFirstName());
        customer.setDob(customerDto.getDob());
        customer.setPhoneNumber(customerDto.getPhoneNumber());
        customer.setOccupation(customerDto.getOccupation());
        customer.setNationality(customerDto.getNationality());
        customer.setNin(customerDto.getNin());
        customer.setBvn(customerDto.getBvn());
        customer.setContactAddress(customerDto.getContactAddress());
        customer.setOfficeAddress(customerDto.getOfficeAddress());
        customer.setNextOfKin(customerDto.getNextOfKin());
        customer.setNextOfKinAddress(customerDto.getNextOfKinAddress());
        customer.setGender(customerDto.getGender());
    }

    public BaseResponse getCustomersDetails(Long customerId) {
        return new BaseResponse(HttpStatus.OK.value(), "successful", getCustomer(customerId));
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            // Split by forward slash
            String[] splitedFileName = fileName.split("/");

            // The last element will be the actual filename
            String imageFile = splitedFileName[splitedFileName.length - 1];
            // Everything before the last slash is the directory
            String uploadDir = String.join("/", Arrays.copyOfRange(splitedFileName, 0, splitedFileName.length - 1));

            Path uploadPath = Paths.get(uploadDir);
            Path file = uploadPath.resolve(imageFile);
            Resource resource = new UrlResource(file.toUri());

            System.out.println("file url " + file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    public BaseResponse updateWalkinCustomer(Long customerId, CustomerDto customerDto, HttpServletRequest request) {

        Customer customer = getCustomer(customerId);

        String baseUrl = getBaseUrl(request);


        if (customerDto.getPassport() != null && !customerDto.getPassport().isEmpty()) {
            updateImage(customer, customerDto, "passport", baseUrl);
        }
        if (customerDto.getSignature() != null && !customerDto.getSignature().isEmpty()) {
            updateImage(customer, customerDto, "signature", baseUrl);
        }

        updateCustomer(customer, customerDto);

        return new BaseResponse(HttpStatus.OK.value(), "successful", customerRepository.save(customer));
    }

    private void updateImage(Customer customer, CustomerDto customerDto, String imageType, String baseUrl) {

        if ("passport".equals(imageType) && customerDto.getPassport() != null) {
            try {
                FileUploadUtil.deleteImage(customer.getPassport());
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "error", e);
            }
            customer.setPassport(saveImage(customerDto.getPassport(), "customer", baseUrl));
        } else if ("signature".equals(imageType) && customerDto.getSignature() != null) {
            try {
                FileUploadUtil.deleteImage(customer.getSignature());
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "error", e);
            }
            customer.setSignature(saveImage(customerDto.getSignature(), "customer", baseUrl));
        }
    }

    public BaseResponse getAllCustomers() {
        return new BaseResponse(HttpStatus.OK.value(), "successful", customerRepository.findAll());
    }

    public BaseResponse suspendCustomer(Long customerId, SuspendCustomerDto suspendCustomerDto) {
        Customer customer = getCustomer(customerId);

        customer.setSuspended(true);
        customer.setReasonForSuspension(suspendCustomerDto.getReasonForSuspension());

        customerRepository.save(customer);

        return new BaseResponse(HttpStatus.OK.value(), "successful", customerRepository.findById(customerId).get());
    }

    private Customer getCustomer(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid customer id"));
    }

    public BaseResponse unBlockCustomer(Long customerId, UnblockCustomerDto unblockCustomerDto) {
        Customer customer = getCustomer(customerId);

        customer.setSuspended(false);
        customer.setReasonForUnblocking(unblockCustomerDto.getReasonForUnblocking());

        customerRepository.save(customer);

        return new BaseResponse(HttpStatus.OK.value(), "successful", customerRepository.findById(customerId).get());
    }
    
    public BaseResponse getAllSuspendedCustomers(boolean suspended) {
        return new BaseResponse(HttpStatus.OK.value(), "successful", customerRepository.findBySuspended(suspended));
    }
}
