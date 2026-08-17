package dev.cankolay.trash.server.module.user

import dev.cankolay.trash.server.common.service.I18nService
import dev.cankolay.trash.server.common.service.RequestInfoService
import dev.cankolay.trash.server.common.web.ApiResponseFactory
import dev.cankolay.trash.server.module.auth.service.AuthService
import dev.cankolay.trash.server.module.user.controller.UserController
import dev.cankolay.trash.server.module.user.entity.Picture
import dev.cankolay.trash.server.module.user.entity.Profile
import dev.cankolay.trash.server.module.user.entity.ProfileGender
import dev.cankolay.trash.server.module.user.entity.User
import dev.cankolay.trash.server.module.user.exception.UserNotFoundException
import dev.cankolay.trash.server.module.user.mapper.toDto
import dev.cankolay.trash.server.module.user.mapper.toProfileDto
import dev.cankolay.trash.server.module.user.repository.UserRepository
import dev.cankolay.trash.server.module.user.service.ProfileService
import dev.cankolay.trash.server.module.user.service.UserService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.util.*

class ProfileTest {

    @Test
    fun `profile entity should default public to false`() {
        val profile = Profile()
        assertFalse(profile.public)
        assertNull(profile.name)
        assertNull(profile.gender)
        assertNull(profile.picture)
    }

    @Test
    fun `profile toDto should map public and picture field correctly`() {
        val picture = Picture(key = "s3-key", url = "https://example.com/pic.jpg")
        val profile = Profile(
            name = "John Doe",
            gender = ProfileGender.MALE,
            picture = picture,
            public = true
        )

        val dto = profile.toDto()
        assertEquals("John Doe", dto.name)
        assertEquals("male", dto.gender)
        assertEquals("https://example.com/pic.jpg", dto.picture)
        assertTrue(dto.public)

        val privateProfile = Profile(
            name = "Jane Doe",
            gender = ProfileGender.FEMALE,
            picture = null,
            public = false
        )
        val privateDto = privateProfile.toDto()
        assertEquals("Jane Doe", privateDto.name)
        assertEquals("female", privateDto.gender)
        assertNull(privateDto.picture)
        assertFalse(privateDto.public)
    }

    @Test
    fun `user toProfileDto should include profile details and picture when public is true`() {
        val picture = Picture(key = "s3-key", url = "https://example.com/avatar.png")
        val profile = Profile(
            name = "Public User",
            gender = ProfileGender.FEMALE,
            picture = picture,
            public = true
        )
        val user = User(
            id = "user-123",
            email = "public@example.com",
            username = "public_user",
            password = "hashed_password",
            profile = profile
        )

        val userProfileDto = user.toProfileDto()
        assertEquals("public_user", userProfileDto.username)
        assertNotNull(userProfileDto.profile)
        assertEquals("Public User", userProfileDto.profile?.name)
        assertEquals("female", userProfileDto.profile?.gender)
        assertEquals("https://example.com/avatar.png", userProfileDto.profile?.picture)
        assertEquals(true, userProfileDto.profile?.public)
    }

    @Test
    fun `user toProfileDto should send only username when public is false`() {
        val picture = Picture(key = "s3-key", url = "https://example.com/avatar.png")
        val profile = Profile(
            name = "Private User",
            gender = ProfileGender.MALE,
            picture = picture,
            public = false
        )
        val user = User(
            id = "user-456",
            email = "private@example.com",
            username = "private_user",
            password = "hashed_password",
            profile = profile
        )

        val userProfileDto = user.toProfileDto()
        assertEquals("private_user", userProfileDto.username)
        assertNull(userProfileDto.profile)
    }

    @Test
    fun `profileService update should update public visibility`() {
        val authService = mock(AuthService::class.java)
        val profileService = ProfileService(auth = authService)

        val user = User(
            id = "user-789",
            email = "test@example.com",
            username = "testuser",
            password = "password",
            profile = Profile(name = "Old Name", public = false)
        )
        `when`(authService.user()).thenReturn(user)

        val updated = profileService.update(name = "New Name", public = true)
        assertEquals("New Name", updated.name)
        assertTrue(updated.public)

        profileService.update(public = false)
        assertFalse(user.profile.public)
    }

    @Test
    fun `userService getByUsername should find by username`() {
        val userRepository = mock(UserRepository::class.java)
        val authService = mock(AuthService::class.java)
        val userService = UserService(
            userRepository = userRepository,
            sessionRepository = mock(),
            connectionRepository = mock(),
            applicationRepository = mock(),
            auth = authService,
            encryptor = mock(),
            securityTokenService = mock(),
            rateLimiter = mock(),
            profilePictureService = mock()
        )

        val targetUser = User(
            id = "id-123",
            email = "user@example.com",
            username = "targetuser",
            password = "password",
            profile = Profile(public = true)
        )

        `when`(userRepository.findByUsername("targetuser")).thenReturn(targetUser)
        `when`(userRepository.findByUsername("unknown")).thenReturn(null)
        `when`(userRepository.findById("unknown")).thenReturn(Optional.empty())

        val byUsername = userService.getByUsername("targetuser")
        assertEquals("targetuser", byUsername.username)

        assertThrows(UserNotFoundException::class.java) {
            userService.getByUsername("unknown")
        }
    }

    @Test
    fun `userController getByUsername should return UserProfileDto with details when public is true`() {
        val i18n = mock(I18nService::class.java)
        `when`(i18n.get(ApiResponseFactory.SUCCESS)).thenReturn("success")
        val responses = ApiResponseFactory(i18n)
        val userService = mock(UserService::class.java)
        val requestInfoService = mock(RequestInfoService::class.java)

        val controller = UserController(
            responses = responses,
            userService = userService,
            requestInfoService = requestInfoService
        )

        val picture = Picture(key = "s3-key", url = "https://s3.aws.com/avatar.jpg")
        val publicUser = User(
            id = "user-1",
            email = "pub@example.com",
            username = "publicuser",
            password = "password",
            profile = Profile(name = "Public Guy", gender = ProfileGender.MALE, picture = picture, public = true)
        )

        `when`(userService.getByUsername("publicuser")).thenReturn(publicUser)

        val response = controller.getByUsername("publicuser")
        assertNotNull(response.body)
        assertEquals("success", response.body?.code)
        assertEquals("publicuser", response.body?.data?.username)
        assertNotNull(response.body?.data?.profile)
        assertEquals("Public Guy", response.body?.data?.profile?.name)
        assertEquals("https://s3.aws.com/avatar.jpg", response.body?.data?.profile?.picture)
        assertEquals("male", response.body?.data?.profile?.gender)
        assertEquals(true, response.body?.data?.profile?.public)
    }

    @Test
    fun `userController getByUsername should return UserProfileDto with profile null when public is false`() {
        val i18n = mock(I18nService::class.java)
        `when`(i18n.get(ApiResponseFactory.SUCCESS)).thenReturn("success")
        val responses = ApiResponseFactory(i18n)
        val userService = mock(UserService::class.java)
        val requestInfoService = mock(RequestInfoService::class.java)

        val controller = UserController(
            responses = responses,
            userService = userService,
            requestInfoService = requestInfoService
        )

        val picture = Picture(key = "s3-key", url = "https://s3.aws.com/avatar.jpg")
        val privateUser = User(
            id = "user-2",
            email = "priv@example.com",
            username = "privateuser",
            password = "password",
            profile = Profile(name = "Private Guy", gender = ProfileGender.MALE, picture = picture, public = false)
        )

        `when`(userService.getByUsername("privateuser")).thenReturn(privateUser)

        val response = controller.getByUsername("privateuser")
        assertNotNull(response.body)
        assertEquals("success", response.body?.code)
        assertEquals("privateuser", response.body?.data?.username)
        assertNull(response.body?.data?.profile)
    }
}
