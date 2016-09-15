package com.jeckep.chat.service;

import com.jeckep.chat.domain.Msg;
import com.jeckep.chat.domain.MsgRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MsgServiceTest {
    @InjectMocks
    MsgService ms;

    @Mock
    MsgRepository mr;

    @Test
    public void getAllMsgsFor() throws Exception {
        List<Msg> orgigMsgs = Arrays.asList(new Msg(1,2,"test1"), new Msg(2,1,"test2"));
        when(mr.getAllMsgsFor(1,2)).thenReturn(orgigMsgs);

        List<Msg> msgs =  ms.getAllMsgsFor(1,2);
        verify(mr).getAllMsgsFor(1,2);
        assertThat("Messages equal", msgs, equalTo(orgigMsgs));
    }

    @Before
    public void init(){
        MockitoAnnotations.initMocks(this);
    }
}