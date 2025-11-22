package com.kayky.domain.cashier;

import com.kayky.commons.CashierUtils;
import com.kayky.commons.PageUtils;
import com.kayky.commons.CashierUtils;
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
import static com.kayky.commons.TestConstants.NON_EXISTING_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CashierServiceTest {
    
    private CashierService service;

    @Mock
    private CashierRepository cashierRepository;
    private final CashierMapper cashierMapper = Mappers.getMapper(CashierMapper.class);
    @Mock
    private UserValidator userValidator;
    
    @BeforeEach
    void setUp(){
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

}