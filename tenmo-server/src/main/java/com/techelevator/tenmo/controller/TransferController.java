package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.TransferDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/transfers")
public class TransferController {

    private UserDao userDao;
    private final AccountDao accountDao;
    private TransferDao transferDao;

    public TransferController(UserDao userDao, AccountDao accountDao, TransferDao transferDao) {
        this.userDao = userDao;
        this.accountDao = accountDao;
        this.transferDao = transferDao;
    }

    @GetMapping()
    public List<TransferDto> listOfTransactions(){
        return transferDao.getAllTransfers();
    }

    @PostMapping("/pending")
    public void  newPendingTransfer(@Valid @RequestBody TransferDto transferDto){
        transferDao.createPendingTransfer(transferDto);
    }

    @GetMapping("/myTransactions")
    public List<TransferDto> listOfTransfers(Principal principal){
        int id = userDao.findIdByUsername(principal.getName());
        List<TransferDto> transferDtoList = transferDao.getTransferListByUserId(id);
        return transferDtoList;
    }

    @PostMapping("/myTransactions")
    public void  newTransfer(@Valid @RequestBody TransferDto transferDto){
        transferDao.createTransfer(transferDto);
    }

    @GetMapping("/myTransactions/{id}")
    public TransferDto findTransferById(@PathVariable int id) {
        return transferDao.getTransferByTransferId(id);
    }

    @PostMapping("/send")
    public void sendAmount(@RequestBody TransferDto transferDto) {
        accountDao.subtractingBalance(transferDto.getAmount(),transferDto.getAccountFromId());
    }

    @PostMapping("/receive")
    public void receiveAmount(@RequestBody TransferDto transferDto) {
        accountDao.addingBalance(transferDto.getAmount(),transferDto.getAccountToId());
    }

    @PutMapping("/approved/{id}")
    public TransferDto updateApprovedPendingTransfer(@PathVariable int id){
        int transferId = transferDao.updateApprovedPendingTransfer(id);
        return transferDao.getTransferByTransferId(transferId);
    }

    @PutMapping("/rejected/{id}")
    public TransferDto rejectedPendingTransfer(@PathVariable int id){
        int transferId =  transferDao.rejectedPendingTransfer(id);
        return transferDao.getTransferByTransferId(transferId);
    }
}


