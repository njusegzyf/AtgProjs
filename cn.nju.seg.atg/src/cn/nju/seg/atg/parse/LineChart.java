package cn.nju.seg.atg.parse;

import java.awt.Color;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleInsets;

import cn.nju.seg.atg.model.Coodinate;

public class LineChart {

	/**
	 * 线性拟合函数数据序列
	 */
	private XYSeries series;
	
	/**
	 * 画一张折线图
	 * @param title
	 * @param coodinateList
	 */
	public void showLineChart(String title, List<Coodinate> coodinateList, List<Double> markerList, double effectiveValue)
	{
		this.createLineChart(title, this.createDataset(coodinateList), markerList, effectiveValue);
	}
	
	/**
	 * 创建折线图
	 * @param title
	 * @param dataset
	 */
	private void createLineChart(String title, XYDataset dataset, List<Double> markerList, double effectiveValue)
	{
		JFreeChart chart = ChartFactory.createXYLineChart(
				title,	// 图表标题
				"X",	// X轴的显示标签
				"Y",	// Y轴的显示标签
				dataset,	// 数据集. 就是CategoryDataset类的实例对象
				PlotOrientation.VERTICAL,	// 图表方向：水平、垂直 || 显示方式
				true,	// 是否显示图例(标题)
				true,	// 是否生成提示工具 tooltips || 是否启动热键
				false	// 是否生成URL链接
				);
		chart.setBackgroundPaint(Color.white);	//设置背景色
		
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.lightGray);		//设置数据区背景色
		plot.setAxisOffset(new RectangleInsets(1.0, 1.0, 1.0, 1.0));
		plot.setDomainGridlinePaint(Color.white);		//设置网格竖线颜色
		plot.setRangeGridlinePaint(Color.white);		//设置网格横线颜色
		plot.setDomainZeroBaselinePaint(Color.black);	//设置Y轴颜色
		plot.setDomainZeroBaselineVisible(true);		//设置Y轴可见
		plot.setRangeZeroBaselinePaint(Color.black);	//设置X轴颜色
		plot.setRangeZeroBaselineVisible(true);			//设置X轴可见
		
		XYLineAndShapeRenderer render = (XYLineAndShapeRenderer) plot.getRenderer();
		render.setBaseShapesVisible(true);	//设置数据点可见
		render.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
		render.setBaseItemLabelsVisible(true);	//设置数据点值可见
		
		this.setMarker(markerList, effectiveValue, plot);	//设置标记线
		
		ChartFrame frame = new ChartFrame(title, chart);
		frame.pack();
		frame.setVisible(true);
	}
	
	/**
	 * 创建折线图数据集
	 * @param coodinateList
	 * @return 折线图数据集
	 */
	private XYDataset createDataset(List<Coodinate> coodinateList)
	{
		this.series = new XYSeries("线性拟合函数");
		int coodinateListSize = coodinateList.size();
		for (int i=0; i<coodinateListSize; i++)
		{
			this.series.add(coodinateList.get(i).getX(), coodinateList.get(i).getY());
		}
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(this.series);
		
		return dataset;
	}
	
	/**
	 * 画蓝色标记线
	 * @param markerList
	 */
	private void setMarker(List<Double> markerList, double effectiveValue, XYPlot plot)
	{
		//建构单个特殊间隔标记
        IntervalMarker marker = null;
        int markerListSize = markerList.size();
        for (int i=0; i<markerListSize; i++)
        {
	        marker = new IntervalMarker(markerList.get(i), markerList.get(i));
	        //目标范围名称
//	        marker.setLabel("Target Range 目标范围");
	        //设置字体
//	        marker.setLabelFont(new Font("SansSerif", Font.ITALIC, 11));
	        //区域名称在边界位置
//	        marker.setLabelAnchor(RectangleAnchor.CENTER);
	        //区域名称的起始位置
//	        marker.setLabelTextAnchor(TextAnchor.CENTER_RIGHT);
	        //区域名称的颜色
//	        marker.setPaint(new Color(222, 222, 255, 128));
	        //区域名称的透明度
//	        marker.setAlpha(1.0f);
	        marker.setOutlinePaint(Color.blue);
	        //使区域标记有效
//	        plot.addRangeMarker(target, Layer.FOREGROUND);
	        plot.addDomainMarker(marker, Layer.FOREGROUND);
        }
        
        // 标记最终可用的坐标点
        marker = new IntervalMarker(effectiveValue, effectiveValue);
        marker.setOutlinePaint(Color.red);
        plot.addDomainMarker(marker, Layer.FOREGROUND);
	}
}
