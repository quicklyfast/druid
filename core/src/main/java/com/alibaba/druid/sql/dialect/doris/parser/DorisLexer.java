package com.alibaba.druid.sql.dialect.doris.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.Keywords;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;

import java.util.HashMap;
import java.util.Map;

public class DorisLexer extends Lexer {
    public static final Keywords DEFAULT_STARROCKS_KEYWORDS;

    static {
        Map<String, Token> map = new HashMap<String, Token>();

        map.putAll(Keywords.DEFAULT_KEYWORDS.getKeywords());

        map.put("BITMAP", Token.BITMAP);
        map.put("USING", Token.USING);
        map.put("PARTITION", Token.PARTITION);
        map.put("PROPERTIES", Token.PROPERTIES);

        DEFAULT_STARROCKS_KEYWORDS = new Keywords(map);
    }

    {
        dbType = DbType.doris;
    }

    public DorisLexer(String input) {
        this(input, true, true);
    }

    public DorisLexer(char[] input, int inputLength, boolean skipComment) {
        super(input, inputLength, skipComment);
        super.keywords = DEFAULT_STARROCKS_KEYWORDS;
    }

    public DorisLexer(String input, SQLParserFeature... features) {
        super(input, true);
        this.keepComments = true;
        super.keywords = DEFAULT_STARROCKS_KEYWORDS;

        for (SQLParserFeature feature : features) {
            config(feature, true);
        }
    }

    public DorisLexer(String input, boolean skipComment, boolean keepComments) {
        super(input, skipComment);
        this.skipComment = skipComment;
        this.keepComments = keepComments;
        super.keywords = DEFAULT_STARROCKS_KEYWORDS;
    }
}
