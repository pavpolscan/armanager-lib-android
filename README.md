# armanager-lib-android
Main goal of ARManager - ease life with creation/population/resizing and rotation of Scandit MatrixScan Augmented Reality views for the trackedBarcodes on the screen in case BarcodeTrackingAdvancedLayoutOverlay is used.

This is simple project to test an idea of reference AR implementation with following ideas in mind:
- ready to use
- programmatic AR templates configuration
- table-based AR templates layout
- named aliases for cells contents addressing on different templates
- extensible

This repo contains ARManager library alone (test app is not included)

## Example Application 
Please check out the sample app from this repository 
[https://github.com/pavpolscan/armanager-sample-app-android](https://github.com/pavpolscan/armanager-sample-app-android)

## Installation 
To get a Git project into your build:

** Step 1. Add the JitPack repository to your build file **
Add it in your root build.gradle at the end of repositories:
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
** Step 2. Add the dependency **
```
    dependencies {
	    implementation 'com.github.pavpolscan:armanager-lib-android:Tag'
	}
```

## Run-time Configuration of ARManager
Following are the steps which developer should implement in their app to configure & use ARManager plugin:
- Instantiate ARManager via ARManager.getInstance()
- Create and define layout & cell names for ARViews (pop-up AR elements)
- Associate each ARView template with particular size range of a barcode via arManager.setViewLayoutForRange()
- Use arManager.getARViewFor(trackedBarcode,...
- ...and be absolutely sure that you as developer can provide good UX (user experience) with the layout, size, orientation and style as well as information present on the AR pop-ups at runtime

## Ready to use
Use static public method of ARManager interface to initialize it in your app
```
ARManager arManager=ARManager.getInstance(Context context, DataCaptureView dataCaptureView);
```

Let's suppose that at runtime we need ARManager to produce a view to display information about a parcel delivery. And parcel object is being characterized by following attributes:
- title
- distance to address
- price 
- quantity
- weight

As developers, we'd like to setup a rules and let ARManager decide dynamically what kind of data on what type of View it will be producing for tracked barcode. We'd like ARManager incapsulate the complexity behind sizing, orientation etc.

## Programmatic AR template configuration
 To streamline runtime behavior, first thing after instantiation that developers should do is create set of AR templates (Views) which will be later used to instantiate and display AR elements on the screen
```
int[] largeViewRows= {1,2,3}; 
ARView largeTemplate = arManager.createView(this,largeViewRows);
```
Will create a template with 3 rows:
![image](https://user-images.githubusercontent.com/70104733/145830316-efe61e32-54d3-420c-adf2-cdab3a3442b9.png)

- topmost row will have 1 cell
- 2nd row with 2 cells
- 3rd row with 3 cells

Let's now create a couple more templates, with smaller layout (less cells to display information). 
```
int[] medViewRows= {2,2};
ARView medTemplate = arManager.createView(this,medViewRows);

int[] smallViewRows= {2};
ARView smallTemplate = arManager.createView(this,smallViewRows);       
```
Later one of these 3 templates will be used by ARManager to display information associated with a tracked barcode. Which template view will be used in particular, ARManager will decide automatically based on specific criterials we'll cover in next sections.

## Named aliases for cells contents addressing
At this point, we have 3 templates defined. 

As project team we decide to display all data about parcel object with largeTemplate and shrink down to only most important attributes on medTemplate and smallTemplate:
```
//large template cells aliasing
largeTemplate.setCellName(0,0,"title");
largeTemplate.setCellName(1,0,"dst_label");
largeTemplate.setCellName(1,1,"dst_km");
largeTemplate.setCellName(2,0,"price");
largeTemplate.setCellName(2,1,"qty");
largeTemplate.setCellName(2,2,"weight");
```
So the largeTemplate has following name aliased defined

![image](https://user-images.githubusercontent.com/70104733/145836375-18e3da78-c075-47af-a10f-3fdb007bfdd5.png)

```

//medium template cell name aliasing
medTemplate.setCellName(0,0,"dst_km");
medTemplate.setCellName(0,1,"price");
medTemplate.setCellName(1,0,"qty");
medTemplate.setCellName(1,1,"weight");

//small template cell name aliasing
smallTemplate.setCellName(0,0,"dst_km");
smallTemplate.setCellName(0,1,"weight");
```

Now, we have all 3 template's cells aliased to specific names. 

**Please note that alias names are not unique - same alias name should be used if the same data is planned to be displayed. In our sample _dst_km_ and _weight_ are two most important attributes of a parcel object and they will be displayed across all views**


## Programmatic re-sizing of ARViews
At this point of configuration we have 
- layouts for the view defined
- layout cells are aliased

Let's define rules when the ARManager should display one of 3 configured templates for the tracked barcode. Size of an ARView depends on barcode size:
- if barcode is very small on the screen, less than 0.2% of a screen area, then the smallTemplate for AR element view should be used
- if barcode is of medium area size, i.e. between 0.2% and 1%, then medTemplate should be used
- if barcode area is larger than 1% of a screen area, then largeTemplate should be used
```
arManager.setViewLayoutForRange(new BarcodeAreaRange(0.0f,0.002f),smallTemplate);
arManager.setViewLayoutForRange(new BarcodeAreaRange(0.002f,0.01f),medTemplate);
arManager.setViewLayoutForRange(new BarcodeAreaRange(0.01f,1.0f),largeTemplate);
```


## Programmatic Styling of AR Views

For the better UX, ARView has additional methods for styling. 
```
largeTemplate.setAlpha(0.8f); //set alpha-chanel, so our AR element is a bit transparent
largeTemplate.setHeaderRowCount(1); //how many Header rows there will be
largeTemplate.setHeaderRowStyle(Color.BLACK,Color.GREEN,0.8f); //styling of header row(s)
largeTemplate.setRowsStyle(Color.WHITE,Color.BLACK,0.8f); //styling of non-header rows
```
TODO: Set of existing methods can be extended to support more styling.

## Using ARManager at runtime
Once configuration is done, you can use the ARManager by passing to it 
- TrackedBarcode - contains data about barcode which is being tracked by Scandit BarcodeTracking
- Map<String,String> barcodeValuesMap - strings key/value map which contains data that should be displayed for barcode. ARManager will take only those values, keys for which are present on template and populate with on ARView.
```
@Nullable
@Override
public View viewForTrackedBarcode(@NotNull BarcodeTrackingAdvancedOverlay barcodeTrackingAdvancedOverlay, @NotNull TrackedBarcode trackedBarcode) {
    Map<String,String> barcodeValuesMap=getTestDataForBarcode(trackedBarcode); //mock method to produce some data, replace with your own
    ARView arView=arManager.getARViewFor(trackedBarcode,barcodeValuesMap);
    return arView;
}
```
## Full sample

Please see full code sample of ARManager configuration and use here: [https://github.com/pavpolscan/armanager-sample-app-android/blob/main/MatrixScanSimpleSample/src/main/java/com/scandit/datacapture/matrixscansimplesample/MatrixScanActivity.java](https://github.com/pavpolscan/armanager-sample-app-android/blob/main/MatrixScanSimpleSample/src/main/java/com/scandit/datacapture/matrixscansimplesample/MatrixScanActivity.java)

##TODO's
This sample is incomplete and lacks following functionality:
- change ARView orientation based on device/barcode orientation 
- more styling options for ARViews including footer row(s) styling, corner rounding, border's etc
- this is Android-only implementation, quick and very dirty, it is not suitable for any production app, could only serve as prototype for idea

Ыфьу framework can be developed for iOS as well as MDF's (Flutter, React Native, Xamarin others)