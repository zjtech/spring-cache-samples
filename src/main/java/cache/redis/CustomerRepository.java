/*
 * Copyright (c) 2018 Zjtech. All rights reserved.
 * This material is the confidential property of Zjtech or its
 * licensors and may be used, reproduced, stored or transmitted only in
 * accordance with a valid MIT license or sublicense agreement.
 */

package cache.redis;

import org.springframework.data.keyvalue.repository.KeyValueRepository;

public interface CustomerRepository extends KeyValueRepository<CustomerHash, String> {

}
