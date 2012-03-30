package com.nicta.scoobi.io

import java.util.Arrays._
import org.apache.hadoop.mapreduce._
import org.apache.hadoop.filecache.DistributedCache

case class ConstantStringDataSource(value: String) extends DataSource[String, String, String] {

  def inputFormat: Class[_ <: InputFormat[String, String]] = classOf[ConstantStringInputFormat]
  def inputCheck {}
  def inputConfigure(job: Job) {
    job.getConfiguration.set("mapred.constant.string", "value")
    DistributedCache.addCacheFile(new java.net.URI("string"), job.getConfiguration)
  }
  def inputSize: Long = value.size
  val inputConverter = ConstantStringInputConverter(value)

  case class ConstantStringInputSplit(value: String) extends InputSplit {
    def getLength = value.size
    def getLocations = Array[String]()
  }

  case class ConstantStringRecordReader() extends RecordReader[String, String] {
    def initialize(split: InputSplit, context: TaskAttemptContext) {}
    def nextKeyValue() = false
    def getCurrentKey = value
    def getCurrentValue = value
    def getProgress = 0.0f
    def close() {}
  }

  trait ConstantStringInputFormat extends InputFormat[String, String] {
    def getSplits(context: JobContext) = asList()
    def createRecordReader(split: InputSplit, context: TaskAttemptContext) = ConstantStringRecordReader()
  }

  case class ConstantStringInputConverter(value: String) extends InputConverter[String, String, String] {
    def fromKeyValue(context: this.type#InputContext, key: String, v: String) = value
  }
}

