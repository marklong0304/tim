package com.travelinsurancemaster.services;

import com.travelinsurancemaster.model.dto.Company;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.repository.CompanyRepository;
import com.travelinsurancemaster.services.security.LoginService;
import com.travelinsurancemaster.services.security.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CompanyService {

    private static final Logger log = LoggerFactory.getLogger(CompanyService.class);

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private LoginService loginService;

    @Transactional(readOnly = true)
    public List<Company> getAll() {
        log.debug("Getting all companies");
        return companyRepository.findByDeletedNull();
    }

    @Transactional(readOnly = true)
    public Company getById(Long id) {
        log.debug("Getting company by id: {}", id);
        return companyRepository.getOne(id);
    }

    @Transactional(readOnly = true)
    public List<Company> getCompanyByNameLike(String filter) {
        log.debug("Getting companies by filter: " + filter);
        return companyRepository.findByNameLikeIgnoreCaseAndDeletedNullOrderByName('%' + filter + '%');
    }

    public Company save(Company company) {
        log.debug("Saving company to db: {}", company.getName());
        return companyRepository.saveAndFlush(company);
    }

    public void delete(Long id) {
        log.debug("Delete company with id: {}", id);
        Company company = getById(id);
        if(company == null) {
            log.debug("Company with id: {} is absent", id);
            return;
        }
        //Delete all company's users
        List<User> users = company.getUsers();
        if(users != null && users.size() > 0) {
            log.debug("Delete users with ids: " + users.stream().map(u -> u.getId().toString()).collect(Collectors.joining(", ")));
        }
        users.forEach(u -> userService.delete(u));
        //Set company deleted by the currently logged in user
        company.setDeleted(new Date());
        company.setDeletedBy(loginService.getLoggedInUser().getEmail());
        companyRepository.saveAndFlush(company);
    }
}