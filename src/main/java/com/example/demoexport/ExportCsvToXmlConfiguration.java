package com.example.demoexport;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.xstream.XStreamMarshaller;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ExportCsvToXmlConfiguration {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    private static String nameInputCSVFile = "UPLOAD_C-000127317_C-000127070_customer_specific.csv";
    private static String nameOutputXMlFile = "UPLOAD_C-000127317_C-000127070_customer_specific.xml";


    @Bean
    SysOutItemWriter itemWriter() {
        return new SysOutItemWriter();
    }

    @Bean
//    @StepScope
    //@Value("#{jobParameters[myparameter]}") String myparameter
    ItemReader<StickerInfo> csvFileItemReader() {
//        System.out.println("---- my parameter is "+ myparameter);
        FlatFileItemReader<StickerInfo> csvFileReader = new FlatFileItemReader<>();
        csvFileReader.setResource(new ClassPathResource("data/"+nameInputCSVFile));
        csvFileReader.setLinesToSkip(1);

        LineMapper<StickerInfo> stickerInfoLineMapper = createStickerInfoLineMapper();
        csvFileReader.setLineMapper(stickerInfoLineMapper);

        return csvFileReader;
    }

    private LineMapper<StickerInfo> createStickerInfoLineMapper() {
        DefaultLineMapper<StickerInfo> stickerInfoLineMapper = new DefaultLineMapper<>();

        LineTokenizer stickerInfoLineTokenizer = createStickerInfoLineTokenizer();
        stickerInfoLineMapper.setLineTokenizer(stickerInfoLineTokenizer);

        FieldSetMapper<StickerInfo> stickerInfoInformationMapper = createStickerInfoInformationMapper();
        stickerInfoLineMapper.setFieldSetMapper(stickerInfoInformationMapper);

        return stickerInfoLineMapper;
    }

    private LineTokenizer createStickerInfoLineTokenizer() {
        DelimitedLineTokenizer studentLineTokenizer = new DelimitedLineTokenizer();
        studentLineTokenizer.setDelimiter(";");
        studentLineTokenizer.setNames(new String[]{"labelId","hiddenCode","batch","spoolNo","projectNumber","type","version","supplier"});
        return studentLineTokenizer;
    }

    private FieldSetMapper<StickerInfo> createStickerInfoInformationMapper() {
        BeanWrapperFieldSetMapper<StickerInfo> studentInformationMapper = new BeanWrapperFieldSetMapper<>();
        studentInformationMapper.setTargetType(StickerInfo.class);
        return studentInformationMapper;
    }


    @Bean(destroyMethod="")
//    @StepScope
    public StaxEventItemWriter<StickerInfo> goodItemWriter() throws Exception {
        XStreamMarshaller marshaller = new XStreamMarshaller();
        Map<String, Class> aliases = new HashMap<>();
        aliases.put("stickerinfo", StickerInfo.class);
        marshaller.setAutodetectAnnotations(true);
        marshaller.setAliases(aliases);
        StaxEventItemWriter<StickerInfo> itemWriter = new StaxEventItemWriter<>();
        itemWriter.setRootTagName("printstickerjob");
        Map<String,String> attributes = new HashMap<>();
        attributes.put("transfertdt","2018-10-25");
        itemWriter.setRootElementAttributes(attributes);
        itemWriter.setMarshaller(marshaller);
        itemWriter.setResource(new FileSystemResource("src/main/resources/result/"+nameOutputXMlFile));
        itemWriter.afterPropertiesSet();
        return itemWriter;
    }

    @Bean
    MyStepListener myListener(){
        return new MyStepListener();
    }


    @Bean
    public Step step() throws Exception {
        return stepBuilderFactory.get("step")
                .<StickerInfo, StickerInfo>chunk(1000000)
                .reader(csvFileItemReader())
                .writer(goodItemWriter())
                .listener(myListener())
                .build();
    }

    @Bean(name = "myJob")
    public Job job() throws Exception {
        return jobBuilderFactory.get("job")
                .incrementer(new RunIdIncrementer())
                .start(step())
                .build();
    }
}
