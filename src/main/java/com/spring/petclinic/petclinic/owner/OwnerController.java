package com.spring.petclinic.petclinic.owner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Collection;

@Controller
public class OwnerController {

    private final OwnerRepository owners;

    @Autowired
    public OwnerController(OwnerRepository ownerRepository) {
        this.owners = ownerRepository;
    }

    @GetMapping("/owners/find")
    public String findOwnersPage(Model model) {
        model.addAttribute(new Owner());
        return "owners/findOwners";
    }

    @GetMapping("/owners")
    public String processFindOwnersForm(Owner owner, BindingResult result, Model model) {
        //if no last name parameter, return all owners
        if(owner.getLastName() == null) {
            owner.setLastName("");
        }

        Collection<Owner> results = this.owners.findByLastNameContainingIgnoreCase(owner.getLastName());

        if(results.isEmpty()) {
            result.rejectValue("lastName", "not found", "not found");
            return "owners/findOwners";
        }

        model.addAttribute("selections", results);
        return "owners/ownersList";
    }

    @GetMapping("/owners/new")
    public String newOwnerPage(Model model) {
        model.addAttribute(new Owner());
        return "owners/createOrUpdateOwnerForm";
    }

    @PostMapping("/owners/new")
    public String processNewOwnerForm(@Valid Owner owner, BindingResult result) {
        if(result.hasErrors()) {
            return "owners/createOrUpdateOwnerForm";
        }
        owners.save(owner);
        return "redirect:/owners/" + owner.getId();
    }

    @GetMapping("/owners/{ownerId}")
    public String ownerDetailsPage(@PathVariable("ownerId") int ownerId, Model model) {
        model.addAttribute(owners.findById(ownerId));
        return "owners/ownerDetails";
    }

    @GetMapping("/owners/{ownerId}/edit")
    public String editOwnerPage(@PathVariable("ownerId") int ownerId, Model model) {
        model.addAttribute(owners.findById(ownerId));
        return "owners/createOrUpdateOwnerForm";
    }

    @PostMapping("/owners/{ownerId}/edit")
    public String processEditOwnerForm(@PathVariable("ownerId") int ownerId, @Valid Owner owner,
                                       BindingResult result) {
        if(result.hasErrors()) {
            return "owners/createOrUpdateOwnerForm";
        }
        owner.setId(ownerId);
        owners.save(owner);
        return "redirect:/owners/" + owner.getId();
    }
}
