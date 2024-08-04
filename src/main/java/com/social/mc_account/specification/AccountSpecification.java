package com.social.mc_account.specification;

import com.social.mc_account.dto.SearchDTO;
import com.social.mc_account.model.Account;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public interface AccountSpecification {
    static Specification<Account> findWithFilter(SearchDTO filter){
        return Specification.where(byFirstname(filter.getFirstName())
                .and(byLastname(filter.getLastName()))
                .and(byCity(filter.getCity()))
                .and(byCountry(filter.getCountry()))
                .and(byIsBlocked(filter.isBlocked()))
                .and(byStatusCode(filter.getStatusCode())))
                .and(byAgeToFrom(filter.getAgeTo(), filter.getAgeFrom()));
    }

    static Specification<Account> byFirstname(String firstname){
        return (root, query, criteriaBuilder) -> {
            if(firstname == null){
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("firstName"), firstname);
        };
    }

    static Specification<Account> byLastname(String lastname){
        return (root, query, criteriaBuilder) -> {
            if(lastname == null){
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("lastName"), lastname);
        };
    }

    static Specification<Account> byCity(String city){
        return (root, query, criteriaBuilder) -> {
            if(city == null){
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("city"), city);
        };
    }

    static Specification<Account> byCountry(String country){
        return (root, query, criteriaBuilder) -> {
            if(country == null){
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("country"), country);
        };
    }

    static Specification<Account> byIsBlocked(Boolean isBlocked){
        return (root, query, criteriaBuilder) -> {
            if(isBlocked == null){
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("isBlocked"), isBlocked);
        };
    }

    static Specification<Account> byStatusCode(String statusCode){
        return (root, query, criteriaBuilder) -> {
            if(statusCode == null){
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("statusCode"), statusCode);
        };
    }


    public static Specification<Account> byAgeToFrom(Integer ageTo, Integer ageFrom) {
        return (Root<Account> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            LocalDate now = LocalDate.now();

            if (ageTo == null && ageFrom == null) {
                return criteriaBuilder.conjunction();
            } else if (ageTo == null) {
                LocalDate birthDateFrom = now.minusYears(ageFrom);
                return criteriaBuilder.lessThanOrEqualTo(root.get("birthDate"), birthDateFrom);
            } else if (ageFrom == null) {
                LocalDate birthDateTo = now.minusYears(ageTo);
                return criteriaBuilder.greaterThanOrEqualTo(root.get("birthDate"), birthDateTo);
            }

            LocalDate birthDateTo = now.minusYears(ageTo);
            LocalDate birthDateFrom = now.minusYears(ageFrom);
            return criteriaBuilder.and(
                    criteriaBuilder.lessThanOrEqualTo(root.get("birthDate"), birthDateFrom),
                    criteriaBuilder.greaterThanOrEqualTo(root.get("birthDate"), birthDateTo)
            );
        };
    }
}