package edu.tcu.cs.hogwarts_artifacts_online.hogwartsuser;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tcu.cs.hogwarts_artifacts_online.hogwartsuser.dto.UserDto;
import edu.tcu.cs.hogwarts_artifacts_online.system.StatusCode;
import org.hamcrest.Matchers;
import org.hibernate.ObjectNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    List<HogwartsUser> users;

    @Value("/api/v1")
    String baseUrl;

    @BeforeEach
    void setUp() throws Exception{
        this.users = new ArrayList<>();

        HogwartsUser u1 = new HogwartsUser();
        u1.setId(1);
        u1.setUsername("john");
        u1.setPassword("123456");
        u1.setEnabled(true);
        u1.setRoles("admin user");
        this.users.add(u1);

        HogwartsUser u2 = new HogwartsUser();
        u2.setId(2);
        u2.setUsername("eric");
        u2.setPassword("654321");
        u2.setEnabled(true);
        u2.setRoles("user");
        this.users.add(u2);

        HogwartsUser u3 = new HogwartsUser();
        u3.setId(3);
        u3.setUsername("tom");
        u3.setPassword("qwerty");
        u3.setEnabled(false);
        u3.setRoles("user");
        this.users.add(u3);
    }



    @Test
    void testFindAllUsersSuccess() throws Exception {
        //Given
        given(this.userService.findAll()).willReturn(this.users);

        //When
        this.mockMvc.perform(get(this.baseUrl + "/users").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(this.users.size())))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].username").value("john"))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].username").value("eric"));
        //Then
    }

    @Test
    void testFindUserByIdSuccess() throws Exception {
        //Given
        given(this.userService.findById(2)).willReturn(this.users.get(1));
        //When and then
        this.mockMvc.perform(get(this.baseUrl + "/users/2").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value(2))
                .andExpect(jsonPath("$.data.username").value("eric"));
    }

    @Test
    void testFindUserByIdNotFound() throws Exception {
        //Given
        given(this.userService.findById(5)).willThrow(new ObjectNotFoundException("user",5));
        //When and then
        this.mockMvc.perform(get(this.baseUrl + "/users/5").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find user with Id 5"))
                .andExpect(jsonPath("$.data").isEmpty());

    }

    @Test
    void testAddUserSuccess() throws Exception {
        HogwartsUser user = new HogwartsUser();
        user.setId(4);
        user.setUsername("lily");
        user.setPassword("123456");
        user.setEnabled(true);
        user.setRoles("admin roles");

        String json = this.objectMapper.writeValueAsString(user);

        user.setId(4);

        //Given
        given(this.userService.save(Mockito.any(HogwartsUser.class))).willReturn(user);

        //When
        this.mockMvc.perform(post(this.baseUrl + "/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Add Success"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.username").value(user.getUsername()))
                .andExpect(jsonPath("$.data.enabled").value(user.isEnabled()))
                .andExpect(jsonPath("$.data.roles").value(user.getRoles()));
    }

    @Test
    void testUpdateUserSuccess() throws Exception {
        //Given
        UserDto userDto = new UserDto(3,
                "tom123",
                false,
                "user"

        );

        HogwartsUser hogwartsUser = new HogwartsUser();
        hogwartsUser.setId(3);
        hogwartsUser.setUsername("tom123");
        hogwartsUser.setEnabled(false);
        hogwartsUser.setRoles("user");

        String json = this.objectMapper.writeValueAsString(userDto);

        given(this.userService.update(eq(3),Mockito.any(HogwartsUser.class))).willReturn(hogwartsUser);
        //When and Then
        this.mockMvc.perform(put(this.baseUrl + "/users/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.id").value(3))
                .andExpect(jsonPath("$.data.username").value(hogwartsUser.getUsername()))
                .andExpect(jsonPath("$.data.enabled").value(hogwartsUser.getRoles()))
                .andExpect(jsonPath("$.data.roles").value(hogwartsUser.getRoles()));
    }

    @Test
    void testUpdateUserErrorWithNonExistentId() throws Exception {
        //Given
        given(this.userService.update(eq(5),Mockito.any(HogwartsUser.class))).willThrow(new ObjectNotFoundException("user", 5));

        UserDto userDto = new UserDto(5,
                "tom123",
                false,
                "user"
        );
        String json = this.objectMapper.writeValueAsString(userDto);


        //When and Then
        this.mockMvc.perform(put(this.baseUrl + "/users/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find user with Id 5"))
                .andExpect(jsonPath("$.data").isEmpty());

    }

    @Test
    void testDeleteArtifactSuccess() throws Exception {
        //Given
        doNothing().when(this.userService).delete(2);
        //When and Then
        this.mockMvc.perform(delete(this.baseUrl + "/users/2")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Delete Success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testDeleteUserErrorWithNonExistentId() throws Exception {
        // Given
        doThrow(new ObjectNotFoundException("user", 5)).when(this.userService).delete(5);

        // When & Then
        this.mockMvc.perform(delete(this.baseUrl + "/users/5")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())  // Đảm bảo HTTP Status Code là 404
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find user with Id 5"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
