package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.TransferDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int createTransfer(TransferDto transferDto) {
        String sql = "insert into transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) values('2','2',?,?,?) returning transfer_id;";
        int idNumber = jdbcTemplate.queryForObject(sql, int.class, transferDto.getAccountFromId(), transferDto.getAccountToId(), transferDto.getAmount());
        return idNumber;
    }

    @Override
    public int createPendingTransfer(TransferDto transferDto) {
        String sql = "insert into transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) values('1','1',?,?,?) returning transfer_id;";
        int idNumber = jdbcTemplate.queryForObject(sql, int.class, transferDto.getAccountFromId(), transferDto.getAccountToId(), transferDto.getAmount());
        return idNumber;
    }

    @Override
    public int updateApprovedPendingTransfer(int id) {
        String sql = "update transfer set transfer_type_id = 2, transfer_status_id = 2 where transfer_id = ? returning transfer_id;";
        int idNumber = jdbcTemplate.queryForObject(sql, int.class, id);
        return idNumber;
    }

    @Override
    public int rejectedPendingTransfer(int id) {
        String sql = "update transfer set transfer_type_id = 1, transfer_status_id = 3 where transfer_id = ? returning transfer_id;";
        int idNumber = jdbcTemplate.queryForObject(sql, int.class, id);
        return idNumber;
    }

    @Override
    public List<TransferDto> getAllTransfers() {
        List<TransferDto> transferDtoList = new ArrayList<>();
        String sql = "select * from transfer;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
        while(result.next()) {
            transferDtoList.add(mapRowToTransfer(result));
        }
        return transferDtoList;
    }

    @Override
    public TransferDto getTransferByTransferId(int id) {
        TransferDto transferDto = null;
        String sql = "select * from transfer where transfer_id = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id);
        if(result.next()){
            transferDto = mapRowToTransfer(result);
        }
        return transferDto;
    }

    @Override
    public List<TransferDto> getTransferListByUserId(int id) {
        List<TransferDto> transferDtoList = new ArrayList<>();
        String sql = "select * from transfer join account a on transfer.account_from = a.account_id where a.user_id = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql,id);
        while(result.next()) {
            transferDtoList.add(mapRowToTransfer(result));
        }
        return transferDtoList;
    }

    @Override
    public List<TransferDto> getTransferListByUsername(String username) {
        List<TransferDto> transferDtoList = new ArrayList<>();
        String sql = "select t.* from transfer t join account a on t.account_from = a.account_id join tenmo_user tu on a.user_id = tu.user_id where tu.username = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql,username);
        while(result.next()) {
            transferDtoList.add(mapRowToTransfer(result));
        }
        return transferDtoList;
    }

    private TransferDto mapRowToTransfer(SqlRowSet rs) {
        TransferDto transferDto = new TransferDto();
        transferDto.setTransferId(rs.getInt("transfer_id"));
        transferDto.setTransferTypeId(rs.getInt("transfer_type_id"));
        transferDto.setTransferStatusId(rs.getInt("transfer_status_id"));
        transferDto.setAccountFromId(rs.getInt("account_from"));
        transferDto.setAccountToId(rs.getInt("account_to"));
        transferDto.setAmount(rs.getBigDecimal("amount"));
        return transferDto;
    }
}
