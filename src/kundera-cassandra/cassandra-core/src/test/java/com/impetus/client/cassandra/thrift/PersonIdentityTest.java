/*******************************************************************************
 * * Copyright 2013 Impetus Infotech.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 ******************************************************************************/
package com.impetus.client.cassandra.thrift;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.impetus.kundera.client.cassandra.persistence.CassandraCli;

/**
 * @author Kuldeep.Mishra
 * 
 */
public class PersonIdentityTest
{

    private EntityManagerFactory emf;

    private EntityManager em;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        CassandraCli.cassandraSetUp();
        CassandraCli.createKeySpace("CompositeCassandra");
                
        emf = Persistence.createEntityManagerFactory("composite_pu");
        em = emf.createEntityManager();
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        em.close();
        emf.close();
        CassandraCli.dropKeySpace("CompositeCassandra");
    }

    @Test
    public void test()
    {
        PhoneId phoneId1 = new PhoneId();
        phoneId1.setPhoneId("A");
        PhoneId phoneId2 = new PhoneId();
        phoneId2.setPhoneId("B");

        Phone phone1 = new Phone();
        phone1.setPhoneId(phoneId1);
        phone1.setPhoneNumber(99533533434l);

        Phone phone2 = new Phone();
        phone2.setPhoneId(phoneId2);
        phone2.setPhoneNumber(9972723678l);

        List<Phone> phones = new ArrayList<Phone>();
        phones.add(phone1);
        phones.add(phone2);

        PersonIdentity identity = new PersonIdentity();
        identity.setPersonId("1");
        identity.setPersonName("KK");
        identity.setPhones(phones);

        em.persist(identity);

        em.clear();
        
        em.close();
        
        em = emf.createEntityManager();

        PersonIdentity foundPerson = em.find(PersonIdentity.class, "1");

        Assert.assertNotNull(foundPerson);
        Assert.assertNotNull(foundPerson.getPhones());
        Assert.assertFalse(foundPerson.getPhones().isEmpty());
        Assert.assertEquals(2,foundPerson.getPhones().size());
        Assert.assertNotNull(foundPerson.getPhones().get(0));
        Assert.assertNotNull(foundPerson.getPhones().get(1));
        Assert.assertNotNull(foundPerson.getPhones().get(0).getPhoneId());
        Assert.assertNotNull(foundPerson.getPhones().get(1).getPhoneId());

        Assert.assertEquals(new Long(99533533434l), foundPerson.getPhones().get(0).getPhoneNumber());
        Assert.assertEquals(new Long(9972723678l), foundPerson.getPhones().get(1).getPhoneNumber());

        Assert.assertEquals("1", foundPerson.getPhones().get(0).getPhoneId().getPersonId());
        
        List<String> phoneIds = new ArrayList<String>();
        phoneIds.add("A");
        phoneIds.add("B");
        Assert.assertTrue(phoneIds.contains(foundPerson.getPhones().get(0).getPhoneId().getPhoneId()));
        Assert.assertEquals("1", foundPerson.getPhones().get(1).getPhoneId().getPersonId());
        Assert.assertNotSame(foundPerson.getPhones().get(0).getPhoneId().getPhoneId(), foundPerson.getPhones().get(1).getPhoneId().getPhoneId());

    }

}
