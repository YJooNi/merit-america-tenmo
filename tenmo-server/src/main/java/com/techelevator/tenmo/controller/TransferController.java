package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.TransferDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/transfers")
public class TransferController {
    private final UserDao userDao;
    private final AccountDao accountDao;
    private final TransferDao transferDao;

    public TransferController(UserDao userDao, AccountDao accountDao, TransferDao transferDao) {
        this.userDao = userDao;
        this.accountDao = accountDao;
        this.transferDao = transferDao;
    }

    @GetMapping()
    public List<TransferDto> listOfTransactions(){
        return transferDao.findAll();
    }

    @GetMapping("/myTransactions")
    public List<TransferDto> listOfTransfers(Principal principal){
        int id = userDao.findIdByUsername(principal.getName());
        List<TransferDto> transferDtoList = transferDao.getTransferListByUserId(id);
        return transferDtoList;
    }

    @PostMapping("/myTransactions")
    public void newTransfer(@Valid @RequestBody TransferDto transferDto){
        transferDao.createTransfer(transferDto);
    }

    @GetMapping("/myTransactions/{id}")
    public TransferDto findTransferById(@PathVariable int id) {
        return transferDao.findByTransferId(id);
    }

    @PostMapping("/receive")
    public void receivedAmount(@RequestBody TransferDto transferDto) {
        accountDao.addingBalance(transferDto.getAmount(),transferDto.getAccountToId());

    }

    @PostMapping("/send")
    public void sendAmount(@RequestBody TransferDto transferDto) {
        accountDao.subtractingBalance(transferDto.getAmount(),transferDto.getAccountFromId());

    }

}
