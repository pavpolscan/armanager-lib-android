package com.scandit.datacapture.barcode.tracking.ui.armanager;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.scandit.datacapture.barcode.tracking.data.TrackedBarcode;
import com.scandit.datacapture.core.common.geometry.Quadrilateral;
import com.scandit.datacapture.core.ui.DataCaptureView;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ARManagerImpl implements ARManager{

    private final Context context;
    private final float displayArea;

    private DataCaptureView dataCaptureView;
    private HashMap<BarcodeAreaRange,ARView> rangeARViewHashMap = new HashMap<>();
    private HashMap<String, BarcodeAreaRange> barcodeAreaOfCode = new HashMap<>();

    private ARManagerImpl(final Context context, final DataCaptureView dataCaptureView){
        this.context = context;
        this.displayArea=1.0f * context.getResources().getDisplayMetrics().widthPixels * context.getResources().getDisplayMetrics().heightPixels;
        this.dataCaptureView=dataCaptureView;
    }

    protected static ARManager getInstance(final Context context, final DataCaptureView dataCaptureView) {
        ARManager arManager = new ARManagerImpl(context, dataCaptureView);
        return arManager;
    }

    @Override
    public ARView createView(final Context context, final @NotNull int[] rows) {
        return new ARView(context, rows);
    }

    @Override
    public ARView setViewLayoutForRange(final BarcodeAreaRange barcodeAreaRange, final ARView arView) {
        rangeARViewHashMap.put(barcodeAreaRange,arView);
        return rangeARViewHashMap.get(barcodeAreaRange);
    }

    @Override
    public ARView setViewLayoutForRange(final float[] barcodeAreaRange, final ARView arView) {
        BarcodeAreaRange barcodeAreaRange1=new BarcodeAreaRange(barcodeAreaRange);
        rangeARViewHashMap.put(barcodeAreaRange1,arView);
        return rangeARViewHashMap.get(barcodeAreaRange1);
    }

    @Override
    public ARView setViewLayoutForRange(final Context context, final float[] barcodeAreaRange, int[] viewRows) {
        BarcodeAreaRange barcodeAreaRange1=new BarcodeAreaRange(barcodeAreaRange);
        ARView arView=createView(context,viewRows);
        rangeARViewHashMap.put(barcodeAreaRange1,arView);
        return arView;
    }

    @Override
    public ARView getARViewFor(final TrackedBarcode trackedBarcode, final Map<String,String> arViewData) {
        Float area=determineBarcodeArea(trackedBarcode);
        BarcodeAreaRange barcodeAreaRange=getBarcodeAreaRangeForArea(area);

        ARView arView=getARViewForArea(area);
        if (arView!=null) {
            arView.fillCellsData(arViewData);
        }

        barcodeAreaOfCode.put(trackedBarcode.getBarcode().getData(),barcodeAreaRange);

        return arView;
    }

    public void removeARView(final TrackedBarcode trackedBarcode){
        barcodeAreaOfCode.remove(trackedBarcode.getBarcode().getData());
    }

    public boolean barcodeAreaInSameRange(final TrackedBarcode trackedBarcode){
        Float area = determineBarcodeArea(trackedBarcode);
        BarcodeAreaRange barcodeAreaRange = barcodeAreaOfCode.get(trackedBarcode.getBarcode().getData());
        return (barcodeAreaRange!=null && barcodeAreaRange.isAreaWithinRange(area)) ? true : false;
    }

    private BarcodeAreaRange getBarcodeAreaRangeForArea(final Float area) {
        for (Map.Entry<BarcodeAreaRange,ARView> rangeARViewEntry: rangeARViewHashMap.entrySet()){
            if (rangeARViewEntry.getKey().isAreaWithinRange(area)){
                return rangeARViewEntry.getKey();
            }
        }
        return null;
    }

    private ARView getARViewForArea(final Float area) {
        for (Map.Entry<BarcodeAreaRange,ARView> rangeARViewEntry: rangeARViewHashMap.entrySet()){
            if (rangeARViewEntry.getKey().isAreaWithinRange(area)){
                return cloneARTemplate(rangeARViewEntry.getValue());
            }
        }
        return null;
    }

    private ARView cloneARTemplate(final ARView template) {
        int[] rows=template.getRows();
        ARView templateInstance=createView(this.context,rows);
        copyTemplateStyle(template,templateInstance);
        for (int i=0; i < rows.length; i++) {
            RowStyle rowStyle=template.getRowStyle(i);
            templateInstance.setRowStyle(i
                    ,rowStyle.getColor()
                    ,rowStyle.getBackgroundColor()
                    ,rowStyle.getAlpha());

            for (int j=0; j < rows[i]; j++) {
                assignNameToTemplateInstanceCell(template, templateInstance, i, j);
                copyCellStyle(template,templateInstance,i,j);
            }
        }

        templateInstance.setCornerRadii(template.getRadii());

        return templateInstance;
    }

    //add to this method properties to copy from template to templateInstance
    //for specific cell styling copy please extend copyCellStyle method
    private void copyTemplateStyle(final ARView template, final ARView templateInstance) {
        templateInstance.setAlpha(template.getAlpha());
    }

    //add to this method properties to copy from templateCell to templateInstanceCell
    //to copy styling of template itself please extend copyTemplateStyle method
    private void copyCellStyle(final ARView template, final ARView templateInstance, final int i, final int j) {
        ARCell templateCell=template.getCell(i,j);
        ARCell instanceCell=templateInstance.getCell(i,j);

        TextView templateView=(TextView)templateCell.getView();
        TextView instanceView=(TextView)instanceCell.getView();

        int backgroundColor = Color.WHITE;
        Drawable background = templateView.getBackground();
        if (background instanceof ColorDrawable){
            backgroundColor = ((ColorDrawable) background).getColor();
        }
        instanceView.setBackgroundColor(backgroundColor);

        int textColor=templateView.getCurrentTextColor();
        instanceView.setTextColor(textColor);

        instanceView.setAlpha(templateView.getAlpha());

        instanceView.setGravity(templateView.getGravity());

        instanceView.setPadding (templateView.getPaddingLeft()
                                ,templateView.getPaddingTop()
                                ,templateView.getPaddingRight()
                                ,templateView.getPaddingBottom());

    }

    private void assignNameToTemplateInstanceCell(final ARView template, final ARView templateInstance, final int i, final int j) {
        ARCell cell=template.getCell(i,j);
        String cellName=cell.getCellName();
        templateInstance.setCellName(i,j,cellName);
    }

    private Float determineBarcodeArea(final TrackedBarcode trackedBarcode) {
        // The coordinates of the code in the image-space.
        // This means that the coordinates correspond to actual pixels in the camera image.
        Quadrilateral barcodePreviewLocation = trackedBarcode.getLocation();
        //this is location of barcode in screen coordinates
        Quadrilateral barcodeViewLocation = dataCaptureView.mapFrameQuadrilateralToView(barcodePreviewLocation);
        float topRightX = barcodeViewLocation.getTopRight().getX();
        float topLeftX = barcodeViewLocation.getTopLeft().getX();
        float bottomRightX = barcodeViewLocation.getBottomRight().getX();
        float bottomLeftX = barcodeViewLocation.getBottomLeft().getX();
        //calculate average width of barcode
        float avgWidth = ((topRightX - topLeftX) + (bottomRightX - bottomLeftX)) / 2;

        float topRightY = barcodeViewLocation.getTopRight().getY();
        float topLeftY = barcodeViewLocation.getTopLeft().getY();
        float bottomRightY = barcodeViewLocation.getBottomRight().getY();
        float bottomLeftY = barcodeViewLocation.getBottomLeft().getY();
        //calculate average height, please note that on screen what is higher has smaller Y-coord value, so zero is at the top
        float avgHeight = (( bottomRightY - topRightY) + ( bottomLeftY - topLeftY)) / 2;

        float avgArea= avgWidth * avgHeight;
        float areaPercentage = avgArea / this.displayArea;

        return Math.abs(areaPercentage);
    }



}
