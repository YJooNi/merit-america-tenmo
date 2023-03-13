package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class TransferDto {
    private int transferId;
    private int transferTypeId;
    private int transferStatusId;
    private int accountFromId;
    private int accountToId;
    private BigDecimal amount;

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public int getTransferTypeId() {
        return transferTypeId;
    }

    public void setTransferTypeId(int transferTypeId) {
        this.transferTypeId = transferTypeId;
    }

    public int getTransferStatusId() {
        return transferStatusId;
    }

    public void setTransferStatusId(int transferStatusId) {
        this.transferStatusId = transferStatusId;
    }

    public int getAccountFromId() {
        return accountFromId;
    }

    public void setAccountFromId(int accountFromId) {
        this.accountFromId = accountFromId;
    }

    public int getAccountToId() {
        return accountToId;
    }

    public void setAccountToId(int accountToId) {
        this.accountToId = accountToId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "TransferDto{" +
                "transfer_id=" + transferId +
                ", transfer_type_id='" + transferTypeId +
                ", transfer_status_id=" + transferStatusId +
                ", account_from=" + accountFromId +
                ", account_to=" + accountToId +
                ", amount=" + amount +
                '}';
    }
}
