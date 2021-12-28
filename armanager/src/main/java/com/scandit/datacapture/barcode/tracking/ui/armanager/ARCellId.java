package com.scandit.datacapture.barcode.tracking.ui.armanager;

import androidx.annotation.Nullable;

import java.util.Objects;

public class ARCellId {
    private final int row, cell;
    private String cellName;

    public ARCellId(int row, int cell) {
        this.row=row;
        this.cell=cell;
    }

    public ARCellId(int row, int cell, @Nullable String cellName) {
        this.row=row;
        this.cell=cell;
        this.cellName=cellName;
    }

    public void setCellName(@Nullable String cellName) {
        this.cellName=cellName;
    }

    @Nullable
    public String getCellName(){
        return this.cellName;
    }

    public int getCell() {
        return cell;
    }

    public int getRow() {
        return row;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, cell, cellName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ARCellId arCellId = (ARCellId) o;
        return row == arCellId.row &&
                cell == arCellId.cell &&
                Objects.equals(cellName, arCellId.cellName);
    }
}
