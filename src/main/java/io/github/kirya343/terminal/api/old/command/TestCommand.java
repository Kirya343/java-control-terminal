package io.github.kirya343.terminal.api.command;

import javax.xml.transform.stream.StreamResult;

import io.github.kirya343.terminal.formatting.DiagnosticTextBuilder;
import io.github.kirya343.terminal.result.CommandResult;
import io.github.kirya343.terminal.result.StreamMode;
import io.github.kirya343.terminal.result.StreamingResult;
import io.github.kirya343.terminal.result.TextResult;

@CommandGroup("cars")
public class TestCommand {

    @Command // вызывается при "cars" 
    public CommandResult getCars(
        @CommandParam(
            value = "query",
            description = "Search query",
            example = "tesla",
            defaultValue = "", //пусто типа
            optional = true
        ) String id, // используется для таких как cars -id ... 
                                 // неуказанные обязательные параметры не запускают метод, а показывают "методичку"
                                 // тут айди не нужен, взято для примера
        @CommandParam(optional = true) boolean verbose // необязательные команды не крашнут методы, булин может указаться просто --verbose, при его остутствии будет false
    ) {
        // вернуть все машины

        DiagnosticTextBuilder builder = new DiagnosticTextBuilder();
        return new TextResult(builder.getContent()); 
    }
    
    @Command("clean") // "cars clean"
    public void cleanCars(
        @CommandParam String query
    ) {
        // машины очистятся, успешный метод выведет "clean cars - ok | {} ms"
    }

    @CommandGroup("{id}") // или например "cars 12"
    public class Playbacks {

        @Command // вызывается при "cars 12"
        public void getCar(
            @CommandPathParam String id // используется для таких как cars
        ) {
            // найти машину по айди
            DiagnosticTextBuilder builder = new DiagnosticTextBuilder();
            return new TextResult(builder.getContent()); 
        }

        @Command("full") // вызывается при "cars 12 give"
        public CommandResult getSome(
            @CommandPathParam Long id //возьмётся из пути
        ) {
            // тут вернётся ВСЯ информация по 12-ой машине машина
            DiagnosticTextBuilder builder = new DiagnosticTextBuilder();
            return new TextResult(builder.getContent()); 
        }
    }
}
