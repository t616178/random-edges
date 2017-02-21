/*
 * Copyright 2017 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.gchq.gaffer.random;

import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.graph.Graph;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.impl.add.AddElements;
import uk.gov.gchq.gaffer.operation.impl.generate.GenerateElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetAllEdges;
import uk.gov.gchq.gaffer.randomdatageneration.Constants;
import uk.gov.gchq.gaffer.randomdatageneration.generator.RandomElementGenerator;
import uk.gov.gchq.gaffer.user.User;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.stream.LongStream;
import java.util.stream.StreamSupport;

public class Random {

    public static void main(final String[] args) throws OperationException, IOException {

        final Graph graph = new Graph.Builder()
                .storeProperties(Random.class.getResourceAsStream("/mockaccumulostore.properties"))
                .addSchema(Random.class.getResourceAsStream("/schema/DataSchema.json"))
                .addSchema(Random.class.getResourceAsStream("/schema/DataTypes.json"))
                .addSchema(Random.class.getResourceAsStream("/schema/StoreTypes.json"))
                .build();

        final long numEntities = Long.parseLong(args[0]);
        final long numEdges = Long.parseLong(args[1]);
        final String filePath = args[2];
        final double[] probabilities = new Constants().rmatProbabilities;

        final RandomElementGenerator generator = new RandomElementGenerator(numEntities, numEdges, probabilities);

        final OperationChain addOpChain = new OperationChain.Builder()
                .first(new GenerateElements.Builder<String>()
                        .generator(generator)
                        .objects(Collections.singleton(""))
                        .build())
                .then(new AddElements())
                .build();

        graph.execute(addOpChain, new User());

        final GetAllEdges getAllEdges = new GetAllEdges.Builder().build();
        CloseableIterable<Edge> edges = graph.execute(getAllEdges, new User());

        final FileWriter fileWriter = new FileWriter(filePath, true);
        final BufferedWriter writer = new BufferedWriter(fileWriter);

        StreamSupport.stream(edges.spliterator(), false)
                     .forEach(e -> LongStream.range(0L, (Long) e.getProperties()
                                                                .get("count"))
                                             .forEach(i -> {
                                                 final StringBuilder sb = new StringBuilder();
                                                 sb.append(e.getSource());
                                                 sb.append(",");
                                                 sb.append(e.getDestination());

                                                 try {
                                                     writer.write(sb.toString());
                                                     writer.newLine();
                                                     writer.flush();
                                                 } catch (IOException e1) {
                                                     e1.printStackTrace();
                                                 }
                                                 System.out.println(sb.toString());
                                             }));

        writer.close();
        fileWriter.close();
    }

}
