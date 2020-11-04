package com.baasexample.demo.service;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.credentials.AccessTokenAuthentication;
import org.springframework.stereotype.Service;
import java.io.IOException;

/**
 * 注释写在这
 *
 * @author Monhey
 */
@Service(value = "k8sClientService")
public class k8sClientService {
    /**
     * 设置默认Client
     */
    public void setK8sClient() throws IOException, ApiException {
     ApiClient client = new ClientBuilder().setBasePath().setVerifyingSsl(false)
                .setAuthentication(new AccessTokenAuthentication(token)).build();
        Configuration.setDefaultApiClient(client);
    }
}


