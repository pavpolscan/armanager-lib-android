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

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.annotation.Size;

import java.util.HashMap;
import java.util.Map;

public class ARView extends LinearLayout {

    private int[] rows;
    private float[] radii={0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f};
    private int headerRowCount=0;
    private int footerRowCount=0;
    private ARCell[][] cellMap = new ARCell[0][0];
    private final HashMap<String,ARCell> cellByNameMap=new HashMap<String,ARCell>();
    private final HashMap<Integer,RowStyle> rowStyleHashMap =new HashMap<Integer, RowStyle>();

    public ARView(Context context) {
        super(context);
    }

    public ARView(Context context, int[] rows) {
        super(context);
        this.rows=rows;
        int columnsCount = getColumnsCount(rows);
        this.cellMap = new ARCell[this.rows.length][columnsCount];
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

    public void setCornersRadius(@Px final int pxTopLeft, @Px final int pxTopRight, @Px final int pxBottomRight, @Px final int pxBottomLeft){
        setCornerRaduisTopLeft(pxTopLeft);
        setCornerRadiusTopRight(pxTopRight);
        setCornerRadiusBottomRight(pxBottomRight);
        setCornerRadiusBottomLeft(pxBottomLeft);
    }

    public void setCornerRaduisTopLeft(@Px final int pixelRadius){
        this.radii[0]=pixelRadius;this.radii[1]=pixelRadius;
    }

    public void setCornerRadiusTopRight(@Px final int pixelRadius){
        this.radii[2]=pixelRadius;this.radii[3]=pixelRadius;
    }

    public void setCornerRadiusBottomRight(@Px final int pixelRadius){
        this.radii[4]=pixelRadius;this.radii[5]=pixelRadius;
    }

    public void setCornerRadiusBottomLeft(@Px final int pixelRadius){
        this.radii[6]=pixelRadius;this.radii[7]=pixelRadius;
    }

    public void setCornerRadii(@Size(8) final float[] radii){
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

    private int getColumnsCount(final int[] rows) {
        int max = Integer.MIN_VALUE;
        for (int i:rows){
           if (max < i) {
               max = i;
           }
        }
        return max;
    }

    private void setCell(final int i, final  int j,final View cellView) {
        ARCell cell=new ARCell(i,j,cellView);
        cellMap[i][j] = cell;
        if (cell.getCellName() != null) {
            cellByNameMap.put(cell.getCellName(), cell);
        }
    }

    public void setCellName(final int rowIndex, final int cellIndex, @Nullable  final String name){
        ARCell arCell=getCell(rowIndex,cellIndex);
        cellByNameMap.remove(arCell.getCellName());
        arCell.setCellName(name);
        if (name != null) {
            cellByNameMap.put(name, arCell);
        }
    }

    public TextView getCellView(final int rowIndex, final int cellIndex){
        TextView result=null;
        ARCell cell=getCell(rowIndex,cellIndex);
        if (cell!=null){
            result=(TextView)cell.getView();
        }
        return result;
    }

    public ARCell getCell(final int rowIndex, final int cellIndex){
        return cellMap[rowIndex][cellIndex];
    }

    public ARCell getCell(final String cellName){
        ARCell arCell=cellByNameMap.get(cellName);
        return arCell;
    }

    public RowStyle getCellStyle(final String cellName){
        ARCell arCell = getCell(cellName);
        int row = arCell.getCellId().getRow();
        RowStyle rowStyle = rowStyleHashMap.get(row);
        return rowStyle;
    }

    public int[] getRows(){
        return this.rows;
    }

    public void fillCellsData(final Map<String,String> displayDataMap){
        for(Map.Entry<String,String> displayDataMapEntry: displayDataMap.entrySet()){
            ARCell targetCell=cellByNameMap.get(displayDataMapEntry.getKey());
            if (targetCell!=null){
                ((TextView)targetCell.getView()).setText(displayDataMapEntry.getValue());
            }
        }
    }

    public void setHeaderRowsCountAndStyle(final int count, @ColorInt final int textColor, @ColorInt final int backgroundColor, final float alpha){
        setHeaderRowCount(count);
        setHeaderRowStyle(textColor, backgroundColor, alpha);
    }

    public void setHeaderRowCount(final int count){
        this.headerRowCount=count;
    }

    public void setHeaderRowStyle(@ColorInt final int textColor, @ColorInt final int backgroundColor, final float alpha) {
        for (int i = 0; i<headerRowCount;i++){
            rowStyleHashMap.put(i,new RowStyle(textColor,backgroundColor,alpha));
            for (int j=0; j < rows[i]; j++) {
                getCellView(i, j).setTextColor(textColor);
                getCellView(i, j).setBackgroundColor(backgroundColor);
                getCellView(i, j).setAlpha(alpha);
            }
        }
    }

    public void setFooterRowsCountAndStyle(final int count, @ColorInt final int textColor, @ColorInt final int backgroundColor, final float alpha){
        setFooterRowCount(count);
        setFooterRowStyle(textColor, backgroundColor, alpha);
    }

    public void setFooterRowCount(final int count){
        this.footerRowCount=count;
    }

    public void setFooterRowStyle(@ColorInt final int textColor, @ColorInt final int backgroundColor, final float alpha) {
        for (int i = rows.length-1; i>(rows.length-footerRowCount-1);i--){
            rowStyleHashMap.put(i,new RowStyle(textColor,backgroundColor,alpha));
            for (int j=0; j < rows[i]; j++) {
                getCellView(i, j).setTextColor(textColor);
                getCellView(i, j).setBackgroundColor(backgroundColor);
                getCellView(i, j).setAlpha(alpha);
            }
        }
    }

    private String getCellDefaultContent(final int row, final int cell){
        return getContext().getString(R.string.ar_cell_content, row, cell);
    }

    public void setRowsStyle(@ColorInt final int textColor, @ColorInt final int backgroundColor, final float alpha){
        for (int i = headerRowCount; i<(rows.length-footerRowCount);i++){
            setRowStyle(i,textColor,backgroundColor,alpha);
        }
    }

    public void setRowStyle(final int row, @ColorInt final int textColor, @ColorInt final int backgroundColor, final float alpha){
        rowStyleHashMap.put(row,new RowStyle(textColor,backgroundColor,alpha));
        for (int j=0; j < rows[row]; j++) {
            getCellView(row, j).setTextColor(textColor);
            getCellView(row, j).setBackgroundColor(backgroundColor);
            getCellView(row, j).setAlpha(alpha);
        }
    }

    public RowStyle getRowStyle(final int rowIndex){
        return rowStyleHashMap.get(rowIndex);
    }

}
