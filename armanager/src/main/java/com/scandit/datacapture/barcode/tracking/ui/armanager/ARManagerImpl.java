package com.scandit.datacapture.barcode.tracking.ui.armanager;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.scandit.datacapture.barcode.tracking.data.TrackedBarcode;
import com.scandit.datacapture.core.common.geometry.Quadrilateral;
import com.scandit.datacapture.core.ui.DataCaptureView;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ARManagerImpl implements ARManager{

    private final ARManager arManager;
    private final Context context;
    private final float displayArea;

    private DataCaptureView dataCaptureView;
    private HashMap<BarcodeAreaRange,ARView> rangeARViewHashMap =new HashMap<>();

    private ARManagerImpl(Context context, DataCaptureView dataCaptureView){
        this.context = context;
        this.displayArea=1.0f * context.getResources().getDisplayMetrics().widthPixels * context.getResources().getDisplayMetrics().heightPixels;
        this.dataCaptureView=dataCaptureView;
        this.arManager=this;
    }

    protected static ARManager getInstance(Context context, DataCaptureView dataCaptureView) {
        ARManager arManager = new ARManagerImpl(context, dataCaptureView);
        return arManager;
    }

    @Override
    public ARView createView(Context context, @NotNull int[] rows) {
        return new ARView(context, rows);
    }

    @Override
    public ARView setViewLayoutForRange(BarcodeAreaRange barcodeAreaRange, ARView arView) {
        rangeARViewHashMap.put(barcodeAreaRange,arView);
        return rangeARViewHashMap.get(barcodeAreaRange);
    }

    @Override
    public ARView setViewLayoutForRange(float[] barcodeAreaRange, ARView arView) {
        BarcodeAreaRange barcodeAreaRange1=new BarcodeAreaRange(barcodeAreaRange);
        rangeARViewHashMap.put(barcodeAreaRange1,arView);
        return rangeARViewHashMap.get(barcodeAreaRange1);
    }

    @Override
    public ARView setViewLayoutForRange(Context context, float[] barcodeAreaRange, int[] viewRows) {
        BarcodeAreaRange barcodeAreaRange1=new BarcodeAreaRange(barcodeAreaRange);
        ARView arView=createView(context,viewRows);
        rangeARViewHashMap.put(barcodeAreaRange1,arView);
        return arView;
    }

//    @Override
//    public List<BarcodeAreaRange> defineRanges(float[] rangePoints) {
//        Arrays.sort(rangePoints);
//        for (int i=0;i<rangePoints.length;i++) {
//            if(i==0){
//                barcodeAreaRanges.add(new BarcodeAreaRange(LOWER_BOUND,rangePoints[i]);
//            }else if(i==(rangePoints.length-1)){
//                barcodeAreaRanges.add(new BarcodeAreaRange(rangePoints[i],UPPER_BOUND);
//            }else {
//                barcodeAreaRanges.add(new BarcodeAreaRange(rangePoints[i-1],rangePoints[i]))
//            }
//        }
//
//        return barcodeAreaRanges;
//    }


    @Override
    public ARView getARViewFor(TrackedBarcode trackedBarcode, Map<String,String> arViewData) {
        Float area=determineBarcodeArea(trackedBarcode);
        ARView arView=getARViewForArea(area);
        if (arView!=null) {
            arView.fillCellsData(arViewData);
        }
        return arView;
    }

    private ARView getARViewForArea(Float area) {
        for (Map.Entry<BarcodeAreaRange,ARView> rangeARViewEntry: rangeARViewHashMap.entrySet()){
            if (rangeARViewEntry.getKey().isAreaWithinRange(area)){
                return cloneARTemplate(rangeARViewEntry.getValue());
            }
        }
        return null;
    }

    private ARView cloneARTemplate(ARView template) {
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
        return templateInstance;
    }

    //add to this method properties to copy from template to templateInstance
    //for specific cell styling copy please extend copyCellStyle method
    private void copyTemplateStyle(ARView template, ARView templateInstance) {
        templateInstance.setAlpha(template.getAlpha());
    }

    //add to this method properties to copy from templateCell to templateInstanceCell
    //to copy styling of template itself please extend copyTemplateStyle method
    private void copyCellStyle(ARView template, ARView templateInstance, int i, int j) {
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

    private void assignNameToTemplateInstanceCell(ARView template, ARView templateInstance, int i, int j) {
        ARCell cell=template.getCell(i,j);
        String cellName=cell.getCellName();
        templateInstance.setCellName(i,j,cellName);
    }

    private Float determineBarcodeArea(TrackedBarcode trackedBarcode) {
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
