package com.social.mc_account.specification;

import com.social.mc_account.dto.SearchDTO;
import com.social.mc_account.model.Account;
import org.springframework.data.jpa.domain.Specification;

public interface AccountSpecification {
    static Specification<Account> findWithFilter(SearchDTO filter){
        return Specification.where(byFirstname(filter.getFirstName())
                .and(byLastname(filter.getLastName()))
                .and(byCity(filter.getCity()))
                .and(byCountry(filter.getCountry()))
                .and(byIsBlocked(filter.isBlocked()))
                .and(byStatusCode(filter.getStatusCode())));
                //.and(byAgeToFrom(filter.getAgeTo(), filter.getAgeFrom())));
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

    static Specification<Account> byAgeToFrom(Integer ageTo, Integer ageFrom){
        return (root, query, criteriaBuilder) -> {
            if(ageTo == null && ageFrom == null){
                return criteriaBuilder.conjunction();
            } else if(ageTo == null){
                return criteriaBuilder.greaterThanOrEqualTo(root.get("ageFrom"), ageFrom);
            } else if(ageFrom == null){
                return criteriaBuilder.greaterThanOrEqualTo(root.get("ageTo"), ageTo);
            }
            return criteriaBuilder.and(
                    criteriaBuilder.greaterThanOrEqualTo(root.get("ageTo"), ageTo),
                    criteriaBuilder.greaterThanOrEqualTo(root.get("ageFrom"), ageFrom)
        );
        };
    }
}