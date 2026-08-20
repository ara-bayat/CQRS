package com.techbank.account.cmd.infrustructure;

import com.techbank.cqrs.core.commands.BaseCommand;
import com.techbank.cqrs.core.commands.CommandHandlerMethod;
import com.techbank.cqrs.core.infrastructure.CommandDispatcher;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class AccountCommandDispatcher implements CommandDispatcher {
    private final Map<Class<? extends BaseCommand>, List<CommandHandlerMethod>> routes= new  HashMap<>();

    @Override
    public <T extends BaseCommand> void registerHandler(Class<T> type, CommandHandlerMethod<T> handler) {
        var handlers=routes.computeIfAbsent(type,c->new ArrayList<>());
        handlers.add(handler);
    }

    @Override
    public void send(BaseCommand command) {
        var handlers=routes.get(command.getClass());
        if(handlers==null|| handlers.isEmpty()){
            throw new RuntimeException("No command handler registered for type "+command.getClass());
        }
        if(handlers.size()>1){
            throw new RuntimeException(String.format("More than one handler for command %s", command.getClass().getName()));
        }
        handlers.getFirst().handle(command);
    }
}
