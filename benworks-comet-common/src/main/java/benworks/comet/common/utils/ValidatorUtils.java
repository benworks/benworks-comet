package benworks.comet.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import benworks.comet.common.data.ErrorCodes;
import benworks.comet.common.model.GeneralException;

/**
 * 校验合法性的工具类
 * 
 * @author benworks
 */
public class ValidatorUtils {

	private static final Log log = LogFactory.getLog(ValidatorUtils.class);

	private static final int CHINESE_MAX = 0x9FFF;

	private static final int CHINESE_MIN = 0x4E00;

	private static final int LOWER_MAX = 0x007A;

	private static final int LOWER_MIN = 0x0061;

	private static final int NUMBER_MAX = 0x0039;

	private static final int NUMBER_MIN = 0x0030;

	private static final int UPPER_MAX = 0x005A;

	private static final int UPPER_MIN = 0x0041;

	private static final Set<String> regKeywords = new HashSet<String>();

	private static final Set<String> talkKeywords = new HashSet<String>();

	public static final String REG_KEYWORDS_FILE = "/reg_keywords.txt";

	public static final String TALK_KEYWORDS_FILE = "/talk_keywords.txt";

	static {
		reset();
	}

	/**
	 * 检查是否包含不允许的字符
	 * 
	 * @param content
	 */
	public static void checkChars(final String content) {
		for (int i = 0; i < content.length(); i++) {
			final int cp = content.charAt(i);
			if (((cp >= NUMBER_MIN) && (cp <= NUMBER_MAX))
					|| ((cp >= UPPER_MIN) && (cp <= UPPER_MAX))
					|| ((cp >= LOWER_MIN) && (cp <= LOWER_MAX))
					|| ((cp >= CHINESE_MIN) && (cp <= CHINESE_MAX))
					|| (cp == '.') || (cp == '-')) {
				continue;
			} else {
				throw new GeneralException(ErrorCodes.ILLEGAL_CHAR);
			}
		}
	}

	/**
	 * 检查是否包含不允许的关键字
	 * 
	 * @param content
	 */
	public static void checkRegKeyword(final String content) {
		for (final String keyword : regKeywords) {
			if (content.indexOf(keyword) != -1) {
				throw new GeneralException(ErrorCodes.ILLEGAL_WORK, keyword);
			}
		}
	}

	/**
	 * 检查是否包含不允许的关键字
	 * 
	 * @param content
	 */
	public static void checkTalkKeyword(final String content) {
		for (final String keyword : talkKeywords) {
			if (content.indexOf(keyword) != -1) {
				throw new GeneralException(ErrorCodes.ILLEGAL_WORK, keyword);
			}
		}
	}

	public static void checkLength(final String content, final int minLen,
			final int maxLen) {
		final int len = getStringLength(content);
		if (len < minLen || len > maxLen) {
			throw new GeneralException(ErrorCodes.ILLEGAL_WORK_LENGTH, minLen,
					maxLen);
		}
	}

	public static int getStringLength(final String content) {
		int len = 0;
		if (content == null) {
			return len;
		}
		for (int i = 0; i < content.length(); i++) {
			final char c = content.charAt(i);
			if (c >= CHINESE_MIN) {
				len += 2;
			} else {
				len++;
			}
		}

		return len;
	}

	private static void init() {
		BufferedReader in = null;
		// 载入注册关键字列表
		try {
			in = new BufferedReader(
					new InputStreamReader(ValidatorUtils.class
							.getResourceAsStream(REG_KEYWORDS_FILE), Charset
							.forName("UTF-8")));
			String keyword;
			while ((keyword = in.readLine()) != null) {
				if (!keyword.trim().equals("") && !keyword.startsWith("#")) {
					regKeywords.add(keyword.toLowerCase());
				}
			}
		} catch (final Exception ex) {
			log.error("classpath:" + REG_KEYWORDS_FILE + " not exists", ex);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (final IOException ex) {
				log.error(ex, ex);
			}
		}

		in = null;
		// 载入聊天关键字列表
		try {
			in = new BufferedReader(
					new InputStreamReader(ValidatorUtils.class
							.getResourceAsStream(TALK_KEYWORDS_FILE), Charset
							.forName("UTF-8")));
			String keyword;
			while ((keyword = in.readLine()) != null) {
				if (!keyword.trim().equals("") && !keyword.startsWith("#")) {
					talkKeywords.add(keyword.toLowerCase());
				}
			}
		} catch (final Exception ex) {
			log.error("classpath:" + TALK_KEYWORDS_FILE + " not exists", ex);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (final IOException ex) {
				log.error(ex, ex);
			}
		}
	}

	/**
	 * 重新初始化
	 */
	public static void reset() {
		regKeywords.clear();
		talkKeywords.clear();
		init();
	}

}
