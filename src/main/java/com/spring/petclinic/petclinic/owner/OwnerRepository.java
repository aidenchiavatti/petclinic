package com.spring.petclinic.petclinic.owner;

import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

public interface OwnerRepository extends Repository<Owner, Integer> {

    @Transactional(readOnly = true)
    Collection<Owner> findByLastNameContainingIgnoreCase(String lastName);

    void save(Owner owner);

    Owner findById(Integer id);
}
