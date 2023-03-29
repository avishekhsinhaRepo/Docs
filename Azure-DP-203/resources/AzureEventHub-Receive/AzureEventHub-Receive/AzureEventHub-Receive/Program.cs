using Azure.Messaging.EventHubs;
using Azure.Messaging.EventHubs.Consumer;
using Azure.Messaging.EventHubs.Producer;
using System;
using System.Text;
using System.Threading.Tasks;

namespace AzureEventHub_Receive
{
    class Program
    {
        private static string connection_string = "Endpoint=sb://gpdeventhubnamespace.servicebus.windows.net/;SharedAccessKeyName=ListenPolicy;SharedAccessKey=8aHnPxM76J2ulpc5ZboCBjaCTVapsSFdb+AEhA7Tsf8=;EntityPath=gpdeventhub";
        private static string consumer_group="$Default";
        private const string eventHubName = "gpdeventhub";
        static async Task Main(string[] args)
        {
            var options = new EventHubConsumerClientOptions();
            options.ConnectionOptions.TransportType = EventHubsTransportType.AmqpWebSockets;

            EventHubConsumerClient _client = new EventHubConsumerClient(consumer_group, connection_string,options);

            await foreach(PartitionEvent _event in _client.ReadEventsAsync())
            {
                Console.WriteLine($"Partition ID {_event.Partition.PartitionId}");
                Console.WriteLine($"Data Offset {_event.Data.Offset}");
                Console.WriteLine($"Sequence Number {_event.Data.SequenceNumber}");
                Console.WriteLine($"Partition Key {_event.Data.PartitionKey}");
                Console.WriteLine(Encoding.UTF8.GetString(_event.Data.EventBody));
            }
            Console.ReadKey();
        }
    }
}
