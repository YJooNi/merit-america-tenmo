package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.TransferDto;

import java.util.List;

public interface TransferDao {

    int createTransfer(TransferDto transferDto);

    int createPendingTransfer(TransferDto transferDto);

    int updateApprovedPendingTransfer(int id);

    int rejectedPendingTransfer(int id);

    List<TransferDto> getAllTransfers();

    TransferDto getTransferByTransferId(int id);

    List<TransferDto> getTransferListByUserId(int id);

    List<TransferDto> getTransferListByUsername(String username);
}
