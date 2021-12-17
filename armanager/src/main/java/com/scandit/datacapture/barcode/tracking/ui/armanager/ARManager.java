package com.scandit.datacapture.barcode.tracking.ui.armanager;

import android.content.Context;
import android.view.View;

import com.scandit.datacapture.barcode.tracking.data.TrackedBarcode;
import com.scandit.datacapture.core.ui.DataCaptureView;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public interface ARManager {

    static ARManager getInstance(Context context, DataCaptureView dataCaptureView) {
        return ARManagerImpl.getInstance(context, dataCaptureView);
    }

    ARView createView(Context context, @NotNull int[] rows);

    ARView setViewLayoutForRange(BarcodeAreaRange barcodeAreaRange,ARView arView);

    ARView setViewLayoutForRange(float[] barcodeAreaRange,ARView arView);

    ARView setViewLayoutForRange(Context context, float[] barcodeAreaRange,int[] viewRows);

    //    List<BarcodeAreaRange> defineRanges(float[] rangePoints);

    ARView getARViewFor(TrackedBarcode trackedBarcode, Map<String,String> arViewData);
}
