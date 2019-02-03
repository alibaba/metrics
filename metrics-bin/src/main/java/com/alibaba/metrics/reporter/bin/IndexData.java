package com.alibaba.metrics.reporter.bin;

public class IndexData{

	private long indexStart;

	private long indexEnd;

	public IndexData(long indexStart, long indexEnd){
		this.indexStart = indexStart;
		this.indexEnd = indexEnd;
	}

	public long getIndexStart() {
		return indexStart;
	}

	public void setIndexStart(long indexStart) {
		this.indexStart = indexStart;
	}

	public long getIndexEnd() {
		return indexEnd;
	}

	public void setIndexEnd(long indexEnd) {
		this.indexEnd = indexEnd;
	}

}

