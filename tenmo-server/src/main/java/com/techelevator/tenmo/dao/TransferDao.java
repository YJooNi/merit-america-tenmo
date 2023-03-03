package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.TransferDto;

import java.util.List;

public interface TransferDao {

    List<TransferDto> findAll();
    TransferDto findByTransferId(int id);

    List<TransferDto> getTransferListByUserId(int id);

    List<TransferDto> findByUsername(String username);

    int createTransfer(TransferDto transferDto);
}
