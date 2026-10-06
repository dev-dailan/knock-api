package com.alsora.knock.domain.choice.repository

import com.alsora.knock.domain.choice.enity.Choice
import org.springframework.data.jpa.repository.JpaRepository

interface ChoiceRepository: JpaRepository<Choice, Long>
