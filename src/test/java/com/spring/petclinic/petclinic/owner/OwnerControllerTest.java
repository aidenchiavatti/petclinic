package com.spring.petclinic.petclinic.owner;

import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(OwnerController.class)
public class OwnerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OwnerRepository ownerRepository;

    @Test
    public void testFindOwnersPage() throws Exception {
        mockMvc.perform(get("/owners/find"))
                .andExpect(status().isOk())
                .andExpect(view().name("owners/findOwners"))
                .andExpect(model().attributeExists("owner"));
    }

    @Test
    public void testProcessFindOwnersForm_OwnersFound() throws Exception {
        given(this.ownerRepository.findByLastNameContainingIgnoreCase("")).willReturn(Lists.newArrayList(new Owner()));
        mockMvc.perform(get("/owners")
            .param("lastName", "")
        )
            .andExpect(status().isOk())
            .andExpect(view().name("owners/ownersList"))
            .andExpect(model().attributeExists("owner"))
            .andExpect(model().attributeExists("selections"));
    }

    @Test
    public void testProcessFindOwnersForm_NoOwnersFound() throws Exception {
        given(this.ownerRepository.findByLastNameContainingIgnoreCase("Not an owner")).willReturn(Lists.newArrayList());
        mockMvc.perform(get("/owners")
            .param("lastName","Not an owner")
        )
            .andExpect(status().isOk())
            .andExpect(view().name("owners/findOwners"))
            .andExpect(model().attributeHasFieldErrors("owner", "lastName"))
            .andExpect(model().attributeHasFieldErrorCode("owner", "lastName", "not found"));
    }

    @Test
    public void testProcessFindOwnersForm_NoLastNameParamPassed() throws Exception {
        given(this.ownerRepository.findByLastNameContainingIgnoreCase("")).willReturn(Lists.newArrayList(new Owner()));
        mockMvc.perform(get("/owners"))
                .andExpect(status().isOk())
                .andExpect(view().name("owners/ownersList"));
    }

    @Test
    public void testCreateOwnerPage() throws Exception {
        mockMvc.perform(get("/owners/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("owners/createOrUpdateOwnerForm"))
                .andExpect(model().attributeExists("owner"));
    }

    @Test
    public void testProcessCreateOwnerFormIsSuccessful() throws Exception {
        mockMvc.perform(post("/owners/new")
                .param("firstName", "Joe")
                .param("lastName", "Bloggs")
                .param("address", "123 Caramel Street")
                .param("city", "London")
                .param("telephone", "01316761638")
        )
                .andExpect(redirectedUrlPattern("/owner/*"));
    }

    @Test
    public void testProcessCreateOwnerFormHasErrors() throws Exception {
        mockMvc.perform(post("/owners/new")
                .param("firstName", "Joe")
                .param("lastName", "Bloggs")
                .param("address", "London")
        )
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("owner"))
                .andExpect(model().attributeHasFieldErrors("owner", "city"))
                .andExpect(model().attributeHasFieldErrors("owner", "telephone"))
                .andExpect(view().name("owners/createOrUpdateOwnerForm"));
    }

    @Test
    public void testOwnerDetailsPage() throws Exception {
        given(ownerRepository.findById(1)).willReturn(new Owner());
        mockMvc.perform(get("/owners/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("owners/ownerDetails"));
    }
}