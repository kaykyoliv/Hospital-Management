package com.kayky.domain.cashier;

import com.kayky.commons.CashierUtils;
import com.kayky.commons.PageUtils;
import com.kayky.core.exception.EmailAlreadyExistsException;
import com.kayky.core.exception.ResourceNotFoundException;
import com.kayky.domain.user.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static com.kayky.commons.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Cashier Service - Unit Tests")
@ExtendWith(MockitoExtension.class)
class CashierServiceTest {

    private CashierService service;

    @Mock
    private CashierRepository repository;
    private final CashierMapper cashierMapper = Mappers.getMapper(CashierMapper.class);
    @Mock
    private UserValidator userValidator;

    @BeforeEach
    void setUp() {
        service = new CashierService(repository, cashierMapper, userValidator);
    }

    @Test
    @DisplayName("findById - Should return CashierBaseResponse when cashier exists")
    void findById_shouldReturnBaseResponse_whenCashierExists() {
        var savedCashier = CashierUtils.savedCashier(EXISTING_ID);
        var expectedResponse = CashierUtils.asBaseResponse(savedCashier);

        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(savedCashier));

        var result = service.findById(EXISTING_ID);

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);

        verify(repository).findById(EXISTING_ID);
    }


    @Test
    @DisplayName("findById - Should throw not-found exception when cashier does not exist")
    void findById_shouldThrowNotFound_whenCashierDoesNotExist() {

        when(repository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(NON_EXISTING_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(CASHIER_NOT_FOUND);

        verify(repository).findById(NON_EXISTING_ID);
    }

    @Test
    @DisplayName("findAll - Should return paged response when cashiers exist")
    void findAll_shouldReturnPagedResponse_whenCashiersExist() {
        PageRequest pageRequest = PageRequest.of(0, 3);
        var cashierList = CashierUtils.cashierList();
        var pagedCashiers = PageUtils.toPage(cashierList);

        when(repository.findAll(pageRequest)).thenReturn(pagedCashiers);

        var result = service.findAll(pageRequest);

        assertThat(result.getTotalElements()).isEqualTo(pagedCashiers.getTotalElements());
        assertThat(result.getTotalPages()).isEqualTo(pagedCashiers.getTotalPages());
        assertThat(result.getCurrentPage()).isEqualTo(pagedCashiers.getNumber());

        var expectedResponse = CashierUtils.baseResponseList();

        assertThat(result.getContent()).usingRecursiveComparison().isEqualTo(expectedResponse);

        verify(repository).findAll(pageRequest);
    }

    @Test
    @DisplayName("save - Should return base response when request is valid")
    void save_shouldReturnBaseResponse_whenCreatingValidCashier() {
        var savedCashier = CashierUtils.savedCashier(EXISTING_ID);

        var expectedResponse = CashierUtils.asBaseResponse(savedCashier);
        var request = CashierUtils.asBaseRequest();

        doNothing().when(userValidator).assertEmailDoesNotExist(request.email());

        when(repository.save(any(Cashier.class))).thenReturn(savedCashier);

        var result = service.save(request);

        assertThat(result).usingRecursiveComparison().isEqualTo(expectedResponse);

        verify(userValidator).assertEmailDoesNotExist(request.email());
        verify(repository).save(any(Cashier.class));
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("save - Should throw email-already-exists exception when email is in use")
    void save_shouldThrowEmailAlreadyExists_whenCreatingWithDuplicateEmail() {
        var request = CashierUtils.asBaseRequest();
        var email = request.email();

        doThrow(new EmailAlreadyExistsException(EMAIL_ALREADY_EXISTS.formatted(email)))
                .when(userValidator)
                .assertEmailDoesNotExist(email);

        assertThatThrownBy(() -> service.save(request))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessage(EMAIL_ALREADY_EXISTS.formatted(email));

        verify(userValidator).assertEmailDoesNotExist(email);
        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("update - Should return base response when request is valid")
    void update_shouldReturnBaseResponse_whenUpdatingValidCashier() {
        var cashierId = EXISTING_ID;
        var savedCashier = CashierUtils.savedCashier(cashierId);

        var expectedResponse = CashierUtils.asBaseResponse(savedCashier);
        var request = CashierUtils.asBaseRequest();

        doNothing().when(userValidator).assertEmailDoesNotExist(request.email(), cashierId);
        when(repository.findById(cashierId)).thenReturn(Optional.of(savedCashier));
        when(repository.save(any(Cashier.class))).thenReturn(savedCashier);

        var result = service.update(request, cashierId);

        assertThat(result).usingRecursiveComparison().isEqualTo(expectedResponse);

        verify(userValidator).assertEmailDoesNotExist(request.email(), cashierId);
        verify(repository).save(any(Cashier.class));
    }

    @Test
    @DisplayName("update - Should throw email-already-exists exception when email is in use")
    void update_shouldThrowEmailAlreadyExists_whenUpdatingWithDuplicateEmail() {
        var cashierId = EXISTING_ID;
        var savedCashier = CashierUtils.savedCashier(cashierId);
        var request = CashierUtils.asBaseRequest();
        var email = request.email();

        when(repository.findById(cashierId)).thenReturn(Optional.of(savedCashier));

        doThrow(new EmailAlreadyExistsException(EMAIL_ALREADY_EXISTS.formatted(email)))
                .when(userValidator)
                .assertEmailDoesNotExist(email, cashierId);

        assertThatThrownBy(() -> service.update(request, cashierId))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessage(EMAIL_ALREADY_EXISTS.formatted(email));

        verify(userValidator).assertEmailDoesNotExist(email, cashierId);
        verify(repository).findById(cashierId);
    }

    @Test
    @DisplayName("update - Should throw not-found exception when cashier does not exist")
    void update_shouldThrowNotFound_whenUpdatingNonExistingCashier() {
        when(repository.findById(NON_EXISTING_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(CashierUtils.asBaseRequest(), NON_EXISTING_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(CASHIER_NOT_FOUND);

        verify(repository).findById(NON_EXISTING_ID);
        verifyNoMoreInteractions(repository);
    }
}