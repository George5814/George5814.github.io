/******************插入排序*******************/
从后向前遍历是否有比取出来的数大的元素

从下标1开始取数字a,与数组内下标x前的元素b依次比较,
	如果a<b并且b的下标不为负数,
		b向后移动一位,
	将取出的数放回空着的位置
否则,插入排序下一个a

	public void myInsertSort_1(int []a) {
		for (int i = 1; i < a.length; i++) {
			int  temp=a[i];
			int j;
			for (j =i-1; j >=0&&a[j]>temp; j--) {
				a[j+1]=a[j];
			}
			a[j+1]=temp;
		}
	}
//时间效率在O(n)到O(n^2)之间,空间复杂度为O(1),直接插入排序算法是稳定的.
//插入排序的每一趟,将一个元素a插入到他前面的一个已排序的子序列,其中采用顺序查找算法寻找a[i]的插入位置,此时,子序列是已经
//排序的且顺序存储,这两条正好符合折半查找的要求.因此,用折半查找代替直接插入排序中的顺序查找,则构成了折半插入排序.


/******************希尔排序*******************/

//希尔排序是分组的直接插入排序 通过改变比较增量的方式实现的直接插入排序
//	
	public void shellSort(int  []a){
		for (int i = 1; i < a.length; i++) {
			int  temp=a[i];
			int j;
			for (j =i-1; j >=0&&a[j]>temp; j--) {
				a[j+1]=a[j];
			}
			a[j+1]=temp;
		}
	}

//希尔排序的空间复杂度为O(1),希尔排序算法不稳定,时间复杂度取决于具体的增量序列


/*************************冒泡排序***********************/
//冒泡排序
	public  void bubbleSort(int []a){
		boolean flag=true;
		for (int i = 1; i < a.length&&flag; i++) {
			flag=false;
			for (int j = 0; j < a.length-i; j++) {
				if(a[j]>a[j+1]){
					int tmp=a[j];
					a[j]=a[j+1];
					a[j+1]=tmp;
					flag=true;
				}
			}
		}
	}
//冒泡排序需要一个辅助空间交换两个元素,空间复杂度O(1).初始数据序列越接近有序,时间效率越高,时间复杂度越低.
//时间效率在O(n)到O(n^2),冒泡排序时稳定的.

/*************************快速排序******************************/

public void quickSort(int a[]){
		quickSort(a, 0, a.length-1);
	}
	
	public void quickSort(int  a[],int begin,int end ){
		//如果 var=a[low]放在此处,在最后一次递归时会越界
		int low=begin;
		int high=end;
		if(begin<end){
			
			int var=a[low];
			while(low!=high){
				while(low<high&&a[high]>=var){
					high--;
				}
				if (low<high) {
					a[low++]=a[high];
					
				}
				
				while(low<high&&a[low]<=var){
					low++;
				}
				if (low<high) {
					a[high--]=a[low];
				}
			}
			a[low]=var;
			quickSort(a, begin, high-1);
			quickSort(a, low+1, end);
		}
	}
//快速排序的比较次数为:O(n*log2N) 时间复杂度O(n*log2N),最坏情况时间复杂度为O(n^2),在n较大,并且数据序列随机排列时
,//快速排序是快速的,n很小时,快速排序是慢的,递归调用中,使用栈保存参数,栈的使用情况与递归调用的次数有关,最好情况下
//空间复杂度为O(log2N),最坏情况下是O(n),平均空间复杂度为O(log2N).快排是不稳定的