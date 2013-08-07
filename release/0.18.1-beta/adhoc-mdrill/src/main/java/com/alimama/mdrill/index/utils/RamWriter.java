package com.alimama.mdrill.index.utils;

import java.io.IOException;
import java.util.Collection;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.index.KeepOnlyLastCommitDeletionPolicy;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

public class RamWriter {

	private RAMDirectory dir;
	private IndexWriter writer;
	private int numDocs = 0;

	public RamWriter() throws IOException {
		dir = new RAMDirectory();
		writer = null;
		numDocs = 0;
	}

	public Directory getDirectory() {
		return dir;
	}

	public int getNumDocs() {
		return numDocs;
	}

	public void process(Collection<Document> docs, Analyzer analyzer)
			throws IOException {
		if (writer == null) {
			writer = createWriter();
		}

		writer.addDocuments(docs, analyzer);
		numDocs += docs.size();
	}

	public void process(RamWriter form) throws IOException {
		if (writer == null) {
			writer = createWriter();
		}

		writer.addIndexesNoOptimize(new Directory[] { form.dir });
		numDocs += form.getNumDocs();
	}


	public void closeWriter()
			throws IOException {
		if (writer != null) {
			
			writer.optimize();
			writer.close();
			writer = null;
			 
		}

	}
	

	public void closeDir() {
		if (this.dir != null) {
			this.dir.close();
			this.dir = null;
		}
	}
	public long totalSizeInBytes() throws IOException {
		if (dir == null) {
			return 0l;
		}
		long size = dir.sizeInBytes();
		if (writer != null) {
			size += writer.ramSizeInBytes();
		}
		return size;
	}

	private IndexWriter createWriter() throws IOException {
		IndexWriter writer = new IndexWriter(dir, null,
				new KeepOnlyLastCommitDeletionPolicy(),
				MaxFieldLength.UNLIMITED);
		writer.setUseCompoundFile(false);
		writer.setMergeFactor(10);
		writer.setTermIndexInterval(16);
		return writer;
	}

}