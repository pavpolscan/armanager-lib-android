# armanager-lib-android
 
Main goal of ARManager - ease life with creation/population/resizing and rotation of Scandit MatrixScan Augmented Reality views for the trackedBarcodes on the screen in case BarcodeTrackingAdvancedLayoutOverlay is used.

This is simple project to test an idea of reference AR implementation with following ideas in mind:
- programmatic AR view templates configuration
- table-based AR templates layout
- named aliases for cells to address cells with same data on different templates
- ready to use yet extensible for your own needs

This repo contains ARManager library alone (test app is not included). 
## Sample App
Please check out the sample application which use ARManager for Scandit Data Capture SDK for Android here [https://github.com/pavpolscan/armanager-sample-app-android](https://github.com/pavpolscan/armanager-sample-app-android)
## Dependencies
ARManager is a dedicated AR-centric library for proprietary Scandit Data Capture SDK for Android v.6.10+. 

While ARManager is an open project, Scandit is a proprietary software. 

Please make sure to read Scandit SDK Android documentation https://docs.scandit.com/data-capture-sdk/android/index.html and check sample apps (https://docs.scandit.com/data-capture-sdk/android/samples/run-samples.html) to get better idea of Scandit capabilities.

## Background & Concept
Scandit SDK provides very open (unconstrained actually) way to implement your own Augmented Reality view for any barcode being tracked on the screen of device. This is done by implementation of [BarcodeTrackingAdvancedOverlayListener.viewForTrackedBarcode()](https://docs.scandit.com/data-capture-sdk/android/barcode-capture/api/ui/barcode-tracking-advanced-overlay-listener.html#method-scandit.datacapture.barcode.tracking.ui.IBarcodeTrackingAdvancedOverlayListener.ViewForTrackedBarcode) method within Activity/Fragment. However, flexibility and opennes comes with a cost of 'from scratch' implementation of AR for your project. 

Plenty small- and mid-size projects would benefit from  simple framework for AR - by solving very common needs of developers.

Let's suppose that at runtime we track barcodes and need to produce AR popup (View) for each barcode in the frame -  it could be a barcode of a parcel or barcode of a product price label on the supermarket shelf etc. 

![image](https://user-images.githubusercontent.com/70104733/147053541-620a298f-77e8-452c-8289-7c36ef81301e.png)
Example above represents 4 different dimensions of AR pop-ups for the supermarket price label barcode depending on the size of tracked barcode. If we as developers ignore re-sizing aspects of AR, it will turn into poor user experience, just imagine how ugly AR could look like if we display AR popups from the left-most image over right-most barcodes - Such AR popups will overlap with each other making not possible to read them in full!

As AR popup size can change dynamically, its contents should adjust to this change as well. In general AR popup is always tied to tracked barcode and tracked barcode has a particular size in pixels (width x height) which is a good initial point to control size of AR popup. If the tracked barcode size change on the screen (camera move farther/closer etc) AR popup should resize and show more/less data about parcel:
- if camera is getting farther away from barcode => small barcode size on screen = small popup = less information (parcel attributes)
- if camera is getting closer to barcode => large barcode size on screen = large popup = more information (parcel attributes)

**Problem #1 which ARManager is solving is dynamic resize of AR popups based on the size of tracked barcode.** And it doesnt stop here

Second problem which appear right after AR popups dynamically resize is change of effective area for the information display:
- if the AR popup is large (right-most image on example above) we can output plenty of data about object we track
- if the AR popup is small (left-most image) we'd need to be very short and display only most important attribute values on the popup, sometimes even shortening it to single character or short term.

**Problem #2 which ARManager is solving is dynamic alignment of data on the AR popups.** It is achieved with use of table-like layouts and named aliases for each table cell. 

Each AR popup has a layout defined with rows. Each row could contain a number of cells:
![image](https://user-images.githubusercontent.com/70104733/147056911-3fdd597d-2b6a-4a9e-aa0f-458f603254f2.png)

Each cell on layout can have a named alias which can be used later to retrieve or set data displayed by this cell:
![image](https://user-images.githubusercontent.com/70104733/147061443-9a6ddf74-a040-455e-babd-03805e33a4a6.png)

In runtime, developer can setup a number of layouts which will be used and setup cell name aliases for each:
![image](https://user-images.githubusercontent.com/70104733/147072569-4e966d92-a393-4be1-af3a-e0e002c673b8.png)

Last thing required would be to define at which visual size of a tracked barcode which particular layout will be rendered. In the example above you can see definition of 3 layouts:
- the largest will be used if tracked barcode size is >5% of a screen size
- medium layout will be used if tracked barcode size is between 5% and 3% of a screen size
- the smallest layout with only single cell will be used for barcodes of a size less than 3% of a screen size

*Please note that view-switching points (5%, 3%) listed here are for illustrative purposes only. Developer should test extensively real sizing of barcodes in the cases where AR is used and align number of layouts and switching points between them. There is no other way to determine the best UX which works for end users except than real-world testing.*

**Ok, if layout is understandable, but why do we need cell aliases?**
Answering short - it allows developer not to check which particular layout is being rendered for particular frame. 

To better illustrate this concept lets review following example. Imagine that parcel object is being characterized by following attributes
- title
- distance to address
- price 
- quantity
- weight

We can define a decorator method for Parcel class to return Map<String,String> of its attributes keys / values

```
public Map<String,String> getParcelData(TrackedBarcode trackedBarcode){
	Parcel myParcel=ParcelService.getParcel(trackedBarcode.getBarcode().getData()); //retrieve parcel object by it's barcode identifier
	
	HashMap<String,String> barcodeValuesMap=new HashMap<>(); //create Map of string key-values
	barcodeValuesMap.put("title",trackedBarcode.getBarcode().getData()); //put parcel barcode values as title 
	barcodeValuesMap.put("dst_label","Distance:"); //put string literal 'Distance:' as dsl_label
	barcodeValuesMap.put("dst_km",String.format("%.2f km", myParcel.getDistanceKilometers())); //put formatted string as distance
	barcodeValuesMap.put("price",String.format("$%.2f",myParcel.getPrice()));
	barcodeValuesMap.put("qty",String.valueOf(Math.round(myParcel.getQuantity())));
	barcodeValuesMap.put("weight",String.format("%.2f",myParcel.getWeight()));

	return barcodeValuesMap;
}
```
and then later use this Map of values to populate ARView
```
Map<String,String> parcelValuesMap=getParcelData(trackedBarcode); // method to retrieve parcel data
ARView arView=arManager.getARViewFor(trackedBarcode,parcelValuesMap); //feeding parcel data to the ARView, ARManager will make sure only those attributes of parcel which are mentioned on the view will get displayed
```

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
	    implementation 'com.github.pavpolscan:armanager-lib-android:RELEASE_TAG'
	}
```
for RELEASE_TAG use tags of this repository (the latest is usually better)

## Run-time Configuration of ARManager
Following are the steps which developer should implement in their app to configure & use ARManager plugin

### Instantiate ARManager 

```
ARManager arManager=ARManager.getInstance(Context context, DataCaptureView dataCaptureView);
```

### Programmaticaly configure ARView template(s) 
After ARManager instantiation let's create set of templates (Views) which will be used to instantiate AR popup views on the screen
```
int[] largeViewRows= {1,2,3}; 
ARView largeTemplate = arManager.createView(this,largeViewRows);
```
For int[] largeViewRows array
- array length defines number of rows in layout
- array element value defines number of cells per row

So 
```
int[] largeViewRows= {1,2,3}; 
```
Is used to define a layout template with 3 rows:
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
Going further these 3 templates will be used by ARManager to display information associated with a tracked barcode. Which template view will be used in particular, ARManager will decide automatically based on specific criterials we'll cover in next sections.

### Set named aliases for cells contents addressing
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

Now, we have all 3 templates defined and cells of each template are aliased to specific names
![image](https://user-images.githubusercontent.com/70104733/147073014-0a1d2419-16f0-4067-8d10-12b6160c1a42.png)

**Please note that alias names are not unique - same alias name should be used if the same data is planned to be displayed. In our sample _dst_km_ and _weight_ are two most important attributes of a parcel object and they will be displayed across all views**


### Define re-sizing points for ARView(s)
At this point of configuration we have 
- layouts for the view defined
- layout cells are aliased with cell names

Let's define rules when the ARManager should display one of 3 configured templates for the tracked barcode. Size of an ARView depends on barcode size:
- if barcode is very small on the screen, less than 3% of a screen area, then the smallTemplate for AR element view should be used
- if barcode is of medium area size, i.e. between 3% and 5%, then medTemplate should be used
- if barcode area is larger than 5% of a screen area, then largeTemplate should be used
```
arManager.setViewLayoutForRange(new BarcodeAreaRange(0.0f,0.002f),smallTemplate);
arManager.setViewLayoutForRange(new BarcodeAreaRange(0.002f,0.01f),medTemplate);
arManager.setViewLayoutForRange(new BarcodeAreaRange(0.01f,1.0f),largeTemplate);
```

### Programmatic Styling of AR Views
For the better UX, ARView has additional methods for styling. 
```
largeTemplate.setAlpha(0.8f); //set alpha-chanel, so our AR element is a bit transparent
largeTemplate.setHeaderRowCount(1); //how many Header rows there will be
largeTemplate.setHeaderRowStyle(Color.BLACK,Color.GREEN,0.8f); //styling of header row(s)
largeTemplate.setRowsStyle(Color.WHITE,Color.BLACK,0.8f); //styling of non-header rows
```
TODO: Set of existing methods can be extended to support more styling.

### Binding ARManager to Scandit SDK BarcodeTracking architecture
Once configuration of ARManager is done, you can use the ARManager by passing to it 
- TrackedBarcode - contains data about barcode which is being tracked by Scandit BarcodeTracking
- Map<String,String> barcodeValuesMap - strings key/value map which contains data that should be displayed for barcode. ARManager will take only those values, keys for which are present on template and populate with on ARView.

**Please use BarcodeTrackingAdvancedOverlayListener.viewForTrackedBarcode method to construct views with ARManager**

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

## TODO's
This sample is incomplete and lacks following functionality:
- change ARView orientation based on device/barcode orientation 
- more styling options for ARViews including footer row(s) styling, corner rounding, border's etc
- this is Android-only implementation, quick and very dirty, it is not suitable for any production app, could only serve as prototype for idea


Same framework can be developed for iOS as well as MDF's (Flutter, React Native, Xamarin others)

