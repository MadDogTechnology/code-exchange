package com.resolute.ordql.poc1.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.resolute.ordql.poc1.dao.PhaseDao;

@Path("/tpc")
public class TPCResource {

  public static final Logger log = LoggerFactory.getLogger(TPCResource.class);

  PhaseDao dao;

  public TPCResource(PhaseDao dao) {
    this.dao = dao;
  }

  // http://localhost:8080/tpc/add/jmLi1TbfqDMLAKEFimuxVl2jlhL01lk85/bQb9NE8RKxTBsjex5pWS3RD16U8F0T

  @GET
  @Path("/add/{name}")
  public void get(@PathParam("name") String name) {

    Preconditions.checkNotNull(name);

    // null equipmentStr will confuse dropwizard's routing.
    String sql = "";
    log.info("Performing query: {}", sql);
    this.dao.addName(name);
  }

}
