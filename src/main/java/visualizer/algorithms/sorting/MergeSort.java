package visualizer.algorithms.sorting;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.shape.Rectangle;
import visualizer.SortingController;
import visualizer.algorithms.Algorithm;
import visualizer.algorithms.Speed;

public class MergeSort extends Algorithm implements Runnable{

	private int[] heights;
	//global indexed are need because of lambda function that is being used to put code into main thread of javaFx
	private int index;
	private int lIndex;
	private int rIndex;

	public MergeSort(Speed sleep, ObservableList<Rectangle> rectList, boolean fromFile, int multi) {
		super(sleep, rectList, fromFile, multi);
		this.heights = new int[rectList.size()];
		for(int i = 0; i < rectList.size();i++){
			this.heights[i] = (int) rectList.get(i).getHeight();
		}
	}

	@Override
	public void run() {
		SortingController.isRunning = true;
		this.mergeSort(this.heights, 0);
		if(SortingController.isRunning==false) return;
		SortingController.isRunning =false;
		this.algorithmDuration(System.currentTimeMillis());
		this.finishSortAlgorithm();
	}

	private void mergeSort(int[] heights, int beginIndex) {
		int length = heights.length;

		//checks if the array can be devided more if not, will return else continue
		if(length <= 1) return;

		int middle = heights.length / 2;
		int[] leftArray = new int[middle];
		int[] rightArray = new int[length-middle];

		int i = 0;
		int j = 0;
		if(SortingController.isRunning==false) return;
		for(; i < length; i++){
			if(i < middle){
				leftArray[i] = heights[i];
			} else {
				rightArray[j] = heights[i];
				j++;
			}
		}
		this.mergeSort(leftArray, beginIndex);
		if(SortingController.isRunning==false) return;
		this.mergeSort(rightArray, beginIndex+middle);
		if(SortingController.isRunning==false) return;
		this.merge(leftArray, rightArray, heights, beginIndex);
		if(SortingController.isRunning==false) return;
	}

	private void merge(int[] leftArray, int[] rightArray, int[] original, int start) {
		int leftSize = original.length / 2;
		int rightSize = original.length - leftSize;
		this.index = 0;
		this.lIndex = 0;
		this.rIndex = 0;
		while(this.lIndex < leftSize && this.rIndex < rightSize) {
			this.colorElements(new int[] {start + this.lIndex}, this.comparingColor);
			this.colorElements(new int[] {start + this.rIndex + leftSize}, this.comparingColor);
			if(leftArray[this.lIndex] < rightArray[this.rIndex]) {
				Platform.runLater(() -> {
				this.rectList.set(this.index+start, new Rectangle(this.width, leftArray[this.lIndex], this.defaultColor));
				});
				original[index] = leftArray[this.lIndex];
				this.sleep(this.sleep);
				this.colorElements(new int[] {start + this.lIndex}, this.defaultColor);
				this.colorElements(new int[] {start + this.rIndex + leftSize}, this.defaultColor);
				this.index++;
				this.lIndex++;
			}
			else {
				Platform.runLater(() -> {
				this.rectList.set(this.index+start, new Rectangle(this.width, rightArray[this.rIndex], this.defaultColor));
				});
				original[index] = rightArray[this.rIndex];
				this.sleep(this.sleep);
				this.rectList.get(start + this.lIndex).setFill(this.defaultColor);
				this.rectList.get(start + this.rIndex + leftSize).setFill(this.defaultColor);
				this.index++;
				this.rIndex++;
			}
			this.counter.increseBy(2);
			if(SortingController.isRunning==false) return;
		}
		
		while(this.lIndex < leftSize) {
			if(SortingController.isRunning==false) return;
			Platform.runLater(() -> {
			this.rectList.set(this.index+start, new Rectangle(this.width, leftArray[this.lIndex], this.defaultColor));
			});
			original[index] = leftArray[this.lIndex];
			this.sleep(this.sleep);
			this.index++;
			this.lIndex++;
			this.counter.increment();
		}
		while(this.rIndex < rightSize) {
			if(SortingController.isRunning==false) return;
			Platform.runLater(() -> {
				this.rectList.set(this.index+start, new Rectangle(this.width, rightArray[this.rIndex], this.defaultColor));
			});
			original[index] = rightArray[this.rIndex];
			this.sleep(this.sleep);
			this.index++;
			this.rIndex++;
			this.counter.increment();
		}
		this.updateTexts();
	}
}
