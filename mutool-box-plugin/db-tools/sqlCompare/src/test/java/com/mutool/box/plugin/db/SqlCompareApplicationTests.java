package com.mutool.box.plugin.db;

import cn.hutool.core.io.FileUtil;
import com.mutool.box.plugin.db.util.SqlAnalyseUtil;
import com.mutool.box.plugin.db.model.CompareAnalyseResult;
import com.mutool.box.plugin.db.model.SqlModel;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class SqlCompareApplicationTests {

	@Test
	void contextLoads() {
		List<SqlModel> sqlList = SqlAnalyseUtil.getFileSqlModelList(FileUtil.file("/Users/connie/Downloads/DRDS_FXGL_MODIFY_DDL_ETAX_V1.8.0.0.4_yingsy.sql"));
		System.out.println(sqlList);
	}

	@Test
	void testSqlCompare(){
		SqlAnalyseUtil.compare("/Users/connie/Downloads/DRDS_FXGL_MODIFY_DDL_ETAX_V1.8.0.0.4_yingsy.sql", "/Users/connie/Downloads/DRDS_FXGL_MODIFY_DDL_ETAX_V1.8.0.0.4_yingsy的副本.sql");
	}

	@Test
	void testAnalyse(){
		List<CompareAnalyseResult> result = SqlAnalyseUtil.analyseCompareResult("/Users/connie/Downloads/DRDS_FXGL_MODIFY_DDL_ETAX_V1.8.0.0.4_yingsy.sql", "/Users/connie/Downloads/DRDS_FXGL_MODIFY_DDL_ETAX_V1.8.0.0.4_yingsy的副本.sql");
		System.out.println(result);
	}

}
