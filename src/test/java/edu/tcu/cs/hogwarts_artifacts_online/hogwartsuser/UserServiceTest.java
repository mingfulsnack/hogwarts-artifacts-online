package edu.tcu.cs.hogwarts_artifacts_online.hogwartsuser;

import org.hibernate.ObjectNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    UserRespository userRespository;

    @InjectMocks
    UserService userService;

    List<HogwartsUser> hogwartsUsers;

    @BeforeEach
    void setUp() {
        HogwartsUser u1 = new HogwartsUser();
        u1.setId(1);
        u1.setUsername("john");
        u1.setPassword("123456");
        u1.setEnabled(true);
        u1.setRoles("admin user");

        HogwartsUser u2 = new HogwartsUser();
        u2.setId(2);
        u2.setUsername("eric");
        u2.setPassword("654321");
        u2.setEnabled(true);
        u2.setRoles("user");

        HogwartsUser u3 = new HogwartsUser();
        u3.setId(3);
        u3.setUsername("tom");
        u3.setPassword("qwerty");
        u3.setEnabled(false);
        u3.setRoles("user");

        this.hogwartsUsers = new ArrayList<>();
        this.hogwartsUsers.add(u1);
        this.hogwartsUsers.add(u2);
        this.hogwartsUsers.add(u3);

    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void testFindAllSuccess() {
        //given
        given(this.userRespository.findAll()).willReturn(this.hogwartsUsers);

        //when
        List<HogwartsUser> actualUsers = this.userService.findAll();

        //then
        assertThat(actualUsers.size()).isEqualTo(this.hogwartsUsers.size());

        //verify
        verify(this.userRespository, times(1)).findAll();
    }

    @Test
    void testFindIdSuccess() {

        //given
        HogwartsUser u = new HogwartsUser();
        u.setId(1);
        u.setUsername("john");
        u.setPassword("123456");
        u.setEnabled(true);
        u.setRoles("admin user");

        given(this.userRespository.findById(1)).willReturn(Optional.of(u));

        //when
        HogwartsUser returnedUser = this.userService.findById(1);

        //then
        assertThat(returnedUser.getId()).isEqualTo(u.getId());
        assertThat(returnedUser.getUsername()).isEqualTo(u.getUsername());
        assertThat(returnedUser.getPassword()).isEqualTo(u.getPassword());
        assertThat(returnedUser.isEnabled()).isEqualTo(u.isEnabled());
        assertThat(returnedUser.getRoles()).isEqualTo(u.getRoles());

        //verify
        verify(this.userRespository, times(1)).findById(1);
    }

    @Test
    void testFindByIdNotFound() {
        //given
        given(this.userRespository.findById(Mockito.any(Integer.class))).willReturn(Optional.empty());

        //when
        Throwable thrown = catchThrowable(() -> {
            HogwartsUser returnedUser = this.userService.findById(1);
        });

        //then
        assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find user with Id 1");

        //verify
        verify(this.userRespository, times(1)).findById(1);
    }

    @Test
    void testSaveSuccess() {

        //given
        HogwartsUser newUser = new HogwartsUser();
        newUser.setUsername("lily");
        newUser.setPassword("123456");
        newUser.setEnabled(true);
        newUser.setRoles("user");

        given(this.userRespository.save(newUser)).willReturn(newUser);

        //when
        HogwartsUser returnedUser = this.userService.save(newUser);

        //then
        assertThat(returnedUser.getUsername()).isEqualTo(newUser.getUsername());
        assertThat(returnedUser.getPassword()).isEqualTo(newUser.getPassword());
        assertThat(returnedUser.isEnabled()).isEqualTo(newUser.isEnabled());
        assertThat(returnedUser.getRoles()).isEqualTo(newUser.getRoles());

        //verify
        verify(this.userRespository, times(1)).save(newUser);
    }

    @Test
    void testUpdateSuccess() {

        //given
        HogwartsUser oldUser = new HogwartsUser();
        oldUser.setId(1);
        oldUser.setUsername("join");
        oldUser.setPassword("123456");
        oldUser.setEnabled(true);
        oldUser.setRoles("admin user");

        HogwartsUser update = new HogwartsUser();
        update.setUsername("join - update");
        update.setPassword("123456");
        update.setEnabled(true);
        update.setRoles("admin user");

        given(this.userRespository.findById(1)).willReturn(Optional.of(oldUser));
        given(this.userRespository.save(oldUser)).willReturn(oldUser);

        //when
        HogwartsUser updatedUser = this.userService.update(1, update);

        //then
        assertThat(updatedUser.getId()).isEqualTo(1);
        assertThat(updatedUser.getUsername()).isEqualTo(update.getUsername());


        //verify
        verify(this.userRespository, times(1)).findById(updatedUser.getId());
        verify(this.userRespository, times(1)).save(oldUser);
    }

    @Test
    void testUpdateNotFound() {

        //given
        HogwartsUser update = new HogwartsUser();
        update.setUsername("join - update");
        update.setPassword("123456");
        update.setEnabled(true);
        update.setRoles("admin user");

        given(this.userRespository.findById(1)).willReturn(Optional.empty());

        //when
        Throwable thrown = assertThrows(ObjectNotFoundException.class, () ->{
            this.userService.update(1,update);
        });

        //then
        assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                        .hasMessage("Could not find user with Id 1");


        //verify
        verify(this.userRespository, times(1)).findById(1);

    }

    @Test
    void testDeleteSuccess() {

        //given
        HogwartsUser user = new HogwartsUser();
        user.setId(1);
        user.setUsername("join");
        user.setPassword("123456");
        user.setEnabled(true);
        user.setRoles("admin user");

        given(this.userRespository.findById(1)).willReturn(Optional.of(user));
        doNothing().when(this.userRespository).deleteById(1);

        //when
        this.userService.delete(1);

        //then


        //verify
        verify(this.userRespository, times(1)).deleteById(1);

    }

    @Test
    void testDeleteNotFound() {

        //given

        given(this.userRespository.findById(1)).willReturn(Optional.empty());


        //when
        Throwable thrown = assertThrows(ObjectNotFoundException.class, () ->{
            this.userService.delete(1);
        });

        //then
        assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find user with Id 1");


        //verify
        verify(this.userRespository, times(1)).findById(1);

    }

}
