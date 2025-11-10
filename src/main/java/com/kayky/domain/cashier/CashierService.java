package com.kayky.domain.cashier;

import com.kayky.core.exception.ResourceNotFoundException;
import com.kayky.core.pagination.PageResponse;
import com.kayky.core.pagination.PageUtils;
import com.kayky.domain.cashier.request.CashierBaseRequest;
import com.kayky.domain.cashier.response.CashierBaseResponse;
import com.kayky.domain.user.UserValidator;
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
    private final UserValidator userValidator;

   @Transactional(readOnly = true)
   public CashierBaseResponse findById(Long id){
       return cashierRepository.findById(id)
               .map(cashierMapper::toCashierBaseResponse)
               .orElseThrow(() -> {
                   log.warn("Cashier not found with ID {}", id);

                   return new ResourceNotFoundException("Cashier not found");
               });
   }

    @Transactional(readOnly = true)
    public PageResponse<CashierBaseResponse> findAll(Pageable pageable) {
        var paginatedCashiers = cashierRepository.findAll(pageable);
        return PageUtils.mapPage(paginatedCashiers, cashierMapper::toCashierBaseResponse);
    }

    @Transactional
    public CashierBaseResponse save(CashierBaseRequest request){
       userValidator.assertEmailDoesNotExist(request.email());

       var cashierToSave = cashierMapper.toEntity(request);
       var savedCashier = cashierRepository.save(cashierToSave);

        log.info("New cashier saved with ID {}", savedCashier.getId());

       return cashierMapper.toCashierBaseResponse(savedCashier);
    }

    @Transactional
    public CashierBaseResponse update(CashierBaseRequest putRequest, Long id) {
        var cashierToUpdate = cashierRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cannot update: Cashier not found with ID {}", id);
                    return new ResourceNotFoundException("Cashier not found");
                });


        userValidator.assertEmailDoesNotExist(putRequest.email(), id);

        cashierMapper.updateCashierFromRequest(putRequest, cashierToUpdate);
        var updatedCashier = cashierRepository.save(cashierToUpdate);

        log.info("Cashier updated with ID {}", updatedCashier.getId());
        return cashierMapper.toCashierBaseResponse(updatedCashier);
    }

}
