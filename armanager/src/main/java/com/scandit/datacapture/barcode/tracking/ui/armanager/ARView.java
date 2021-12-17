package com.scandit.datacapture.barcode.tracking.ui.armanager;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CancellationException;

public class ARView extends LinearLayout {

    private int[] rows;
    private int columnsCount;
    private int headerRowCount=0;
    private HashMap<ARCellId,ARCell> cellMap=new HashMap<>();
    private HashMap<String,ARCell> cellByNameMap=new HashMap<>();

    public ARView(Context context) {
        super(context);
    }

    public ARView(Context context, int[] rows) {
        super(context);
        this.rows=rows;
        this.columnsCount=getColumnsCount(rows);
        for (int i=0; i < rows.length; i++) {
            LinearLayout row = new LinearLayout(context);
            row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,1.0f));
            row.setOrientation(LinearLayout.HORIZONTAL);
            for (int j=0; j < rows[i]; j++) {
                TextView tv = new TextView(context);
                tv.setTextColor(Color.BLACK);
                tv.setBackgroundColor(Color.WHITE);
                float weight=1.0f/rows[i];
                LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.MATCH_PARENT,weight);
                tv.setPadding(5,2,5,2);
                tv.setLayoutParams(lp);
                tv.setGravity(Gravity.CENTER);
                tv.setText(getCellDefaultContent(i,j));

                row.addView(tv);
                setCell(i,j,tv);

            }
            this.addView(row);
        }

        this.setOrientation(LinearLayout.VERTICAL);
        this.setLayoutParams(
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT
                        ,ViewGroup.LayoutParams.MATCH_PARENT)
        );
    }

    private int getColumnsCount(int[] rows) {
        List l=new LinkedList();
        for (int i:rows){
           l.add(new Integer(i));
        }
        int max=((Integer)Collections.max(l)).intValue();
        return max;
    }

    private void setCell(int i, int j, View cellView) {
        ARCell cell=new ARCell(i,j,cellView);
        cellMap.put(cell.getCellId(),cell);
        cellByNameMap.put(cell.getCellName(),cell);
    }

    public void setCellName(int rowIndex, int cellIndex, String name){
        ARCell arCell=getCell(rowIndex,cellIndex);
        arCell.setCellName(name);
        cellByNameMap.put(name,arCell);
    }

    public TextView getCellView(int rowIndex, int cellIndex){
        TextView result=null;
        ARCell cell=getCell(rowIndex,cellIndex);
        if (cell!=null){
            result=(TextView)cell.getView();
        }
        return result;
    }

    public ARCell getCell(int rowIndex, int cellIndex){

        for(ARCellId cellId:cellMap.keySet()){
            if (cellId.getRow()==rowIndex && cellId.getCell()==cellIndex)
            {
                ARCell target=cellMap.get(cellId);
                return target;
            }
        }
        return null;
    }

    public int[] getRows(){
        return this.rows;
    }

    public void fillCellsData(Map<String,String> displayDataMap){
        for(Map.Entry<String,String> displayDataMapEntry: displayDataMap.entrySet()){
            ARCell targetCell=cellByNameMap.get(displayDataMapEntry.getKey());
            if (targetCell!=null){
                targetCell.setCellContents(displayDataMapEntry.getValue());
            }
        }
    }

    public void setHeaderRowCount(int count){
        this.headerRowCount=count;
    }

    private String getCellDefaultContent(@NotNull int row, @NotNull int cell){
        return "("+String.valueOf(row)+","+String.valueOf(cell)+")";
    }

    public void setHeaderRowStyle(int textColor, int backgroundColor, float alpha) {
        for (int i = 0; i<headerRowCount;i++){
            for (int j=0; j < rows[i]; j++) {
                getCellView(i, j).setTextColor(textColor);
                getCellView(i, j).setBackgroundColor(backgroundColor);
                getCellView(i, j).setAlpha(alpha);
            }
        }
    }

    public void setRowsStyle(int textColor, int backgroundColor, float alpha){
        for (int i = headerRowCount; i<rows.length;i++){
            for (int j=0; j < rows[i]; j++) {
                getCellView(i, j).setTextColor(textColor);
                getCellView(i, j).setBackgroundColor(backgroundColor);
                getCellView(i, j).setAlpha(alpha);
            }
        }
    }

}
