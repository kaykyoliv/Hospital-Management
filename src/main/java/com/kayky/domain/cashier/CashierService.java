package com.kayky.domain.cashier;

import com.kayky.core.exception.ResourceNotFoundException;
import com.kayky.core.pagination.PageResponse;
import com.kayky.core.pagination.PageUtils;
import com.kayky.domain.cashier.response.CashierBaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CashierService {

    private final CashierRepository cashierRepository;
    private final CashierMapper cashierMapper;

   @Transactional(readOnly = true)
   public CashierBaseResponse findById(Long id){
       return cashierRepository.findById(id)
               .map(cashierMapper::toCashierBaseResponse)
               .orElseThrow(() -> {
                   log.warn("Patient not found with ID {}", id);

                   return new ResourceNotFoundException("Cashier not found");
               });
   }

    @Transactional(readOnly = true)
    public PageResponse<CashierBaseResponse> findAll(Pageable pageable) {
        var paginatedPatients = cashierRepository.findAll(pageable);
        return PageUtils.mapPage(paginatedPatients, cashierMapper::toCashierBaseResponse);
    }
}
