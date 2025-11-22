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

@ExtendWith(MockitoExtension.class)
class CashierServiceTest {

    private CashierService service;

    @Mock
    private CashierRepository cashierRepository;
    private final CashierMapper cashierMapper = Mappers.getMapper(CashierMapper.class);
    @Mock
    private UserValidator userValidator;

    @BeforeEach
    void setUp() {
        service = new CashierService(cashierRepository, cashierMapper, userValidator);
    }

    @Test
    @DisplayName("findById: Should return CashierBaseResponse when the cashier exists")
    void findById_ShouldReturnCashierBaseResponse_WhenCashierExists() {
        var savedCashier = CashierUtils.savedCashier(EXISTING_ID);

        when(cashierRepository.findById(EXISTING_ID)).thenReturn(Optional.of(savedCashier));

        var result = service.findById(EXISTING_ID);

        var expectedResponse = CashierUtils.asBaseResponse(savedCashier);

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);

        verify(cashierRepository).findById(EXISTING_ID);
    }


    @Test
    @DisplayName("findById: Should throw ResourceNotFoundException when the Cashier does not exist")
    void findById_ShouldThrowResourceNotFoundException_WhenCashierDoesNotExist() {

        when(cashierRepository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(NON_EXISTING_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(CASHIER_NOT_FOUND);

        verify(cashierRepository).findById(NON_EXISTING_ID);
    }

    @Test
    @DisplayName("findAll: Should return PageResponse when cashiers exist")
    void findAll_ShouldReturnPageResponse_WhenCashierExist() {
        PageRequest pageRequest = PageRequest.of(0, 3);
        var cashierList = CashierUtils.cashierList();
        var pagedCashiers = PageUtils.toPage(cashierList);

        when(cashierRepository.findAll(pageRequest)).thenReturn(pagedCashiers);

        var result = service.findAll(pageRequest);

        assertThat(result.getTotalElements()).isEqualTo(pagedCashiers.getTotalElements());
        assertThat(result.getTotalPages()).isEqualTo(pagedCashiers.getTotalPages());
        assertThat(result.getCurrentPage()).isEqualTo(pagedCashiers.getNumber());

        var expectedResponse = CashierUtils.baseResponseList();

        assertThat(result.getContent()).usingRecursiveComparison().isEqualTo(expectedResponse);

        verify(cashierRepository).findAll(pageRequest);
    }

    @Test
    @DisplayName("save: should return CashierBaseResponse when data is valid")
    void save_ShouldReturnCashierBaseResponse_WhenDataIsValid() {
        var savedCashier = CashierUtils.savedCashier(EXISTING_ID);

        var expectedResponse = CashierUtils.asBaseResponse(savedCashier);
        var request = CashierUtils.asBaseRequest();

        doNothing().when(userValidator).assertEmailDoesNotExist(request.email());

        when(cashierRepository.save(any(Cashier.class))).thenReturn(savedCashier);

        var result = service.save(request);

        assertThat(result).usingRecursiveComparison().isEqualTo(expectedResponse);

        verify(userValidator).assertEmailDoesNotExist(request.email());
    }

    @Test
    @DisplayName("save: Should throw EmailAlreadyExistsException when email is already in use")
    void save_ShouldThrowEmailAlreadyExistsException_WhenEmailAlreadyExists() {
        var request = CashierUtils.asBaseRequest();
        var email = request.email();

        doThrow(new EmailAlreadyExistsException(EMAIL_ALREADY_EXISTS.formatted(email)))
                .when(userValidator)
                .assertEmailDoesNotExist(email);

        assertThatThrownBy(() -> service.save(request))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessage(EMAIL_ALREADY_EXISTS.formatted(email));

        verify(userValidator).assertEmailDoesNotExist(email);
        verifyNoInteractions(cashierRepository);
    }

}