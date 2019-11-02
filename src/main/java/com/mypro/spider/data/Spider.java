package com.mypro.spider.data;

import org.apache.hadoop.io.Text;

/**
 * @author fgzhong
 * @since 2019/1/9
 */
public interface Spider {

	public static final String ORIGINAL_CHAR_ENCODING = "OriginalCharEncoding";

	public static final String CHAR_ENCODING_FOR_CONVERSION = "CharEncodingForConversion";

	public static final String SIGNATURE_KEY = "nutch.content.digest";

	public static final String SEGMENT_NAME_KEY = "nutch.segment.name";

	public static final String SCORE_KEY = "nutch.crawl.score";

	public static final String GENERATE_TIME_KEY = "_ngt_";

	public static final Text WRITABLE_GENERATE_TIME_KEY = new Text(
			GENERATE_TIME_KEY);

	public static final Text PROTOCOL_STATUS_CODE_KEY = new Text("nutch.protocol.code");

	public static final String PROTO_STATUS_KEY = "_pst_";

	public static final Text WRITABLE_PROTO_STATUS_KEY = new Text(
			PROTO_STATUS_KEY);

	public static final String FETCH_TIME_KEY = "_ftk_";

	public static final String FETCH_STATUS_KEY = "_fst_";

	/**
	 * Sites may request that search engines don't provide access to cached
	 * documents.
	 */
	public static final String CACHING_FORBIDDEN_KEY = "caching.forbidden";

	/** Show both original forbidden content and summaries (default). */
	public static final String CACHING_FORBIDDEN_NONE = "none";

	/** Don't show either original forbidden content or summaries. */
	public static final String CACHING_FORBIDDEN_ALL = "all";

	/** Don't show original forbidden content, but show summaries. */
	public static final String CACHING_FORBIDDEN_CONTENT = "content";

	public static final String REPR_URL_KEY = "_repr_";

	public static final Text WRITABLE_REPR_URL_KEY = new Text(REPR_URL_KEY);

	/** Used by AdaptiveFetchSchedule to maintain custom fetch interval */
	public static final String FIXED_INTERVAL_KEY = "fixedInterval";

	public static final Text WRITABLE_FIXED_INTERVAL_KEY = new Text(
			FIXED_INTERVAL_KEY);

	 /** For progress of job. Used by the Nutch REST service */
	public static final String STAT_PROGRESS = "progress";
	/**Used by Nutch REST service */
	public static final String CRAWL_ID_KEY = "storage.crawl.id";
	/** Argument key to specify location of the seed url dir for the REST endpoints **/
	public static final String ARG_SEEDDIR = "url_dir";
	/** Argument key to specify name of a seed list for the REST endpoints **/
	public static final String ARG_SEEDNAME = "seedName";
	/** Argument key to specify the location of crawldb for the REST endpoints **/
	public static final String ARG_CRAWLDB = "crawldb";
	/** Argument key to specify the location of linkdb for the REST endpoints **/
	public static final String ARG_LINKDB = "linkdb";
	/** Name of the key used in the Result Map sent back by the REST endpoint **/
	public static final String VAL_RESULT = "result";
	/** Argument key to specify the location of a directory of segments for the REST endpoints.
	 * Similar to the -dir command in the bin/nutch script **/
	public static final String ARG_SEGMENTDIR = "segment_dir";
	/** Argument key to specify the location of individual segment or list of segments for the REST endpoints. The behavior differs for diffirent endpoints: CrawlDb, LinkDb and Indexing Jobs take list of segments, Fetcher and Parse segment take one segment **/
	public static final String ARG_SEGMENTS = "segment";
	/** Argument key to specify the location of hostdb for the REST endpoints **/
	public static final String ARG_HOSTDB = "hostdb";
	/** Title key in the Pub/Sub event metadata for the title of the parsed page*/
	public static final String FETCH_EVENT_TITLE = "title";
	/** Content-type key in the Pub/Sub event metadata for the content-type of the parsed page*/
	public static final String FETCH_EVENT_CONTENTTYPE = "content-type";
	/** Score key in the Pub/Sub event metadata for the score of the parsed page*/
	public static final String FETCH_EVENT_SCORE = "score";
	/** Fetch time key in the Pub/Sub event metadata for the fetch time of the parsed page*/
	public static final String FETCH_EVENT_FETCHTIME = "fetchTime";
	/** Content-lanueage key in the Pub/Sub event metadata for the content-language of the parsed page*/
	public static final String FETCH_EVENT_CONTENTLANG = "content-language";
}
