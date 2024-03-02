/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.path.to.plugin;

import com.carrotsearch.randomizedtesting.annotations.ThreadLeakScope;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.opensearch.action.admin.cluster.node.info.NodeInfo;
import org.opensearch.action.admin.cluster.node.info.NodesInfoResponse;
import org.opensearch.action.admin.cluster.node.info.PluginsAndModules;
import org.opensearch.client.Request;
import org.opensearch.client.Response;
import org.opensearch.plugins.Plugin;
import org.opensearch.plugins.PluginInfo;
import org.opensearch.test.OpenSearchIntegTestCase;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;

@ThreadLeakScope(ThreadLeakScope.Scope.NONE)
@OpenSearchIntegTestCase.ClusterScope(scope = OpenSearchIntegTestCase.Scope.SUITE)
public class RenamePluginIT extends OpenSearchIntegTestCase {

    @Override
    protected Collection<Class<? extends Plugin>> nodePlugins() {
        return Collections.singletonList(RenamePlugin.class);
    }

    public void testPluginInstalled() throws IOException, ParseException {
        Response response = getRestClient().performRequest(new Request("GET", "/_cat/plugins"));
        String body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

        logger.info("response body: {}", body);
        assertThat(body, containsString("rename"));
    }

    public void testPluginNames() {
        NodesInfoResponse response = client().admin().cluster().prepareNodesInfo().clear().addMetric("plugins").get();
        assertEquals(0, response.failures().size());
        assertFalse(response.getNodes().isEmpty());
        List<NodeInfo> nodes = response.getNodes();
        for (NodeInfo ni : nodes) {
            assertNotNull(ni.getInfo(PluginsAndModules.class));

            // The following test would fail!
            /*
            assertEquals(
                    1,
                    ni.getInfo(PluginsAndModules.class).getPluginInfos().stream().filter(
                            pluginInfo -> pluginInfo.getName().equals("rename")
                    ).count()
            );
            */

            // Normally, I would expect that the plugin deployed into a node will report its name
            // consistently. But it turns out that in some cases the plugin returns value of its classname
            // instead of the 'name'.
            //
            // Depending on what role the node has, but it seems that there are nodes that have also
            // two additional plugins deployed: MockNioTransportPlugin and MockHttpTransport$TestPlugin
            // and in this case the plugins name is a classname (also their description is a "classpath plugin").
            //
            // You can setup a breakpoint on the line below and investigate all plugins from particular node.

            List<PluginInfo> pluginInfos = ni.getInfo(PluginsAndModules.class).getPluginInfos();
            if (pluginInfos.size() > 1) {
                assertEquals(
                        1,
                        ni.getInfo(PluginsAndModules.class).getPluginInfos().stream().filter(
                                pluginInfo -> pluginInfo.getName().equals("org.opensearch.path.to.plugin.RenamePlugin")
                        ).count()
                );
            } else if (pluginInfos.size() == 1) {
                assertEquals(
                        1,
                        ni.getInfo(PluginsAndModules.class).getPluginInfos().stream().filter(
                                pluginInfo -> pluginInfo.getName().equals("rename")
                        ).count()
                );
            } else {
                fail("Unexpected");
            }
        }
    }
}
