# compose the enviorment cluster
docker-compose -f docker-compose.yml up -d 

# send over userprofile and arival rate data
curl -L -X POST "http://192.168.178.2:8080/uploaduserprofile" -F "file=@\"./example_user_profile.yml\""
curl -L -X POST "http://192.168.178.2:8080/uploadarrivalrateprofile" -F "file=@\"./ExampleSynchronizerArrivalRates.csv\""

#start experiment
curl -L -X POST "http://localhost:8080/startloadtest" -H "Content-Type: application/json" --data-raw "{\"wrapped_settings\":{\"num_users\":128,\"num_sending_threads\":16,\"random_batch_times\":true,\"random_seed\":5,\"warmup_duration_s\":30,\"warmup_rate\":0.0,\"warmup_pause_s\":5,\"randomize_users\":true,\"timeout_ms\":-1,\"log_response_validity_errors\":false,\"log_responses\":false,\"arrival_rates\":[],\"request_profile\":{\"user_requests\":null,\"services\":[]}},\"arrival_rate_duration\":60,\"arrival_rate_start\":1.0,\"arrival_rate_end\":60.0,\"load_generator_ips\":\"192.168.178.2\",\"user_request_profile_file_name\":\"example_user_profile.yml\",\"arrival_rate_profile_file_name\":\"ExampleSynchronizerArrivalRates.csv\",\"run_label\":\"\"}"

# docker start loadgenerator-slave