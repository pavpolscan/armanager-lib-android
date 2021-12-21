package com.scandit.datacapture.barcode.tracking.ui.armanager;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ARView extends LinearLayout {

    private int[] rows;
    private float[] radii={0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f};
    private int columnsCount;
    private int headerRowCount=0;
    private int footerRowCount=0;
    private HashMap<ARCellId,ARCell> cellMap=new HashMap<ARCellId,ARCell>();
    private HashMap<String,ARCell> cellByNameMap=new HashMap<String,ARCell>();
    private HashMap<Integer,RowStyle> rowStyleHashMap =new HashMap<Integer, RowStyle>();

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

    public float[] getRadii() {
        return this.radii;
    }

    public void setCornersRadius(int pxTopLeft, int pxTopRight, int pxBottomRight, int pxBottomLeft){
        setCornerRaduisTopLeft(pxTopLeft);
        setCornerRadiusTopRight(pxTopRight);
        setCornerRadiusBottomRight(pxBottomRight);
        setCornerRadiusBottomLeft(pxBottomLeft);
    }

    public void setCornerRaduisTopLeft(int pixelRadius){
        this.radii[0]=pixelRadius;this.radii[1]=pixelRadius;
    }

    public void setCornerRadiusTopRight(int pixelRadius){
        this.radii[2]=pixelRadius;this.radii[3]=pixelRadius;
    }

    public void setCornerRadiusBottomRight(int pixelRadius){
        this.radii[4]=pixelRadius;this.radii[5]=pixelRadius;
    }

    public void setCornerRadiusBottomLeft(int pixelRadius){
        this.radii[6]=pixelRadius;this.radii[7]=pixelRadius;
    }

    public void setCornerRadii(float[] radii){
        this.radii=radii;
        ARCell topLeft, topRight, bottomLeft, bottomRight = null;

        boolean isTopSingleCell= getRows()[0] > 1 ? false : true ;
        boolean isBottomSingleCell= getRows()[getRows().length-1] > 1 ? false : true;

        topLeft=getCell(0,0);
        bottomLeft = getCell(getRows().length-1,0);

        if (isTopSingleCell) {
            GradientDrawable shapeTop=new GradientDrawable();
            shapeTop.setCornerRadii(new float[] {radii[0],radii[1],radii[2],radii[3],0.0f,0.0f,0.0f,0.0f});
            shapeTop.setColor(getRowStyle(0).getBackgroundColor());
            topLeft.getView().setBackground(shapeTop);
        }
        else{
            topRight=getCell(0,getRows()[0] > 1 ? getRows()[0]-1 : 0);

            GradientDrawable shapeTopLeft=new GradientDrawable();
            shapeTopLeft.setCornerRadii(new float[] {radii[0],radii[1],0.0f,0.0f,0.0f,0.0f,0.0f,0.0f});
            shapeTopLeft.setColor(getRowStyle(0).getBackgroundColor());
            topLeft.getView().setBackground(shapeTopLeft);

            GradientDrawable shapeTopRight=new GradientDrawable();
            shapeTopRight.setCornerRadii(new float[] {0.0f,0.0f,radii[2],radii[3],0.0f,0.0f,0.0f,0.0f,0.0f,0.0f});
            shapeTopRight.setColor(getRowStyle(0).getBackgroundColor());
            topRight.getView().setBackground(shapeTopRight);
        }

        if (isBottomSingleCell) {
            GradientDrawable shapeBottom=new GradientDrawable();
            shapeBottom.setCornerRadii(new float[] {0.0f,0.0f,0.0f,0.0f,radii[4],radii[5],radii[6],radii[7]});
            shapeBottom.setColor(getRowStyle(this.getRows().length - 1).getBackgroundColor());
            bottomLeft.getView().setBackground(shapeBottom);
        }
        else{
            bottomRight = getCell(getRows().length-1,getRows()[getRows().length-1]-1);

            GradientDrawable shapeBottomRight=new GradientDrawable();
            shapeBottomRight.setCornerRadii(new float[] {0.0f,0.0f,0.0f,0.0f,radii[4],radii[5],0.0f,0.0f});
            shapeBottomRight.setColor(getRowStyle(this.getRows().length - 1).getBackgroundColor());
            bottomRight.getView().setBackground(shapeBottomRight);

            GradientDrawable shapeBottomLeft=new GradientDrawable();
            shapeBottomLeft.setCornerRadii(new float[] {0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,radii[6],radii[7]});
            shapeBottomLeft.setColor(getRowStyle(this.getRows().length - 1).getBackgroundColor());
            bottomLeft.getView().setBackground(shapeBottomLeft);
        }
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

    public ARCell getCell(String cellName){
        ARCell arCell=cellByNameMap.get(cellName);
        return arCell;
    }

    public RowStyle getCellStyle(String cellName){
        ARCell arCell = getCell(cellName);
        int row = arCell.getCellId().getRow();
        RowStyle rowStyle = rowStyleHashMap.get(Integer.valueOf(row));
        return rowStyle;
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

    public void setHeaderRowsCountAndStyle(int count, int textColor, int backgroundColor, float alpha){
        setHeaderRowCount(count);
        setHeaderRowStyle(textColor, backgroundColor, alpha);
    }

    public void setHeaderRowCount(int count){
        this.headerRowCount=count;
    }

    public void setHeaderRowStyle(int textColor, int backgroundColor, float alpha) {
        for (int i = 0; i<headerRowCount;i++){
            rowStyleHashMap.put(Integer.valueOf(i),new RowStyle(textColor,backgroundColor,alpha));
            for (int j=0; j < rows[i]; j++) {
                getCellView(i, j).setTextColor(textColor);
                getCellView(i, j).setBackgroundColor(backgroundColor);
                getCellView(i, j).setAlpha(alpha);
            }
        }
    }

    public void setFooterRowsCountAndStyle(int count, int textColor, int backgroundColor, float alpha){
        setFooterRowCount(count);
        setFooterRowStyle(textColor, backgroundColor, alpha);
    }

    public void setFooterRowCount(int count){
        this.footerRowCount=count;
    }

    public void setFooterRowStyle(int textColor, int backgroundColor, float alpha) {
        for (int i = rows.length-1; i>(rows.length-footerRowCount-1);i--){
            rowStyleHashMap.put(Integer.valueOf(i),new RowStyle(textColor,backgroundColor,alpha));
            for (int j=0; j < rows[i]; j++) {
                getCellView(i, j).setTextColor(textColor);
                getCellView(i, j).setBackgroundColor(backgroundColor);
                getCellView(i, j).setAlpha(alpha);
            }
        }
    }

    private String getCellDefaultContent(@NotNull int row, @NotNull int cell){
        return "("+String.valueOf(row)+","+String.valueOf(cell)+")";
    }

    public void setRowsStyle(int textColor, int backgroundColor, float alpha){
        for (int i = headerRowCount; i<(rows.length-footerRowCount);i++){
            setRowStyle(i,textColor,backgroundColor,alpha);
        }
    }

    public void setRowStyle(int row, int textColor, int backgroundColor, float alpha){
        rowStyleHashMap.put(Integer.valueOf(row),new RowStyle(textColor,backgroundColor,alpha));
        for (int j=0; j < rows[row]; j++) {
            getCellView(row, j).setTextColor(textColor);
            getCellView(row, j).setBackgroundColor(backgroundColor);
            getCellView(row, j).setAlpha(alpha);
        }
    }

    public RowStyle getRowStyle(int rowIndex){
        return rowStyleHashMap.get(Integer.valueOf(rowIndex));
    }

}
