package generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.util.Collections;

public class CodeGenerator {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/training?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai";
        String username = "root";
        String password = "root";
        String author = "student";
        String parentPackage = "com.qst.domain";
        String tableName = "song_list";

        FastAutoGenerator.create(url, username, password)
                .globalConfig(builder -> {
                    builder.author(author)
                            .outputDir(System.getProperty("user.dir") + "/domain/src/main/java")
                            .disableOpenDir()
                            .enableSwagger();
                })
                .packageConfig(builder -> {
                    builder.parent(parentPackage)
                            .entity("entity")
                            .service("service")
//                            .serviceImpl("service.impl")//domain中不应该有实现类
                            .mapper("mapper")
                            .controller("controller")
                            .pathInfo(Collections.singletonMap(OutputFile.xml, System.getProperty("user.dir") + "/domain/src/main/resources/mapper"));
                })
                .strategyConfig(builder -> {
                    builder.addInclude(tableName)
                            .entityBuilder()
                            .naming(NamingStrategy.underline_to_camel)
                            .columnNaming(NamingStrategy.underline_to_camel)
                            .enableLombok()
                            .build()
                            .controllerBuilder()
                            .enableRestStyle()
                            .enableHyphenStyle()
                            .build();
                })
                .execute();
    }
}
