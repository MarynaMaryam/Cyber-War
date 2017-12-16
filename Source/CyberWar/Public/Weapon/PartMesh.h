// Fill out your copyright notice in the Description page of Project Settings.

#pragma once

#include "CoreMinimal.h"
#include "UObject/NoExportTypes.h"
#include "PartMesh.generated.h"

/**
 * Базовый класс частей оружия.
 */

UENUM(BlueprintType)
enum class EPartType : uint8
{
	PartType_Body1		UMETA(DisplayName = "Body 1"),
	PartType_Body2		UMETA(DisplayName = "Body 2"),
	PartType_Barrel		UMETA(DisplayName = "Barrel"),
	PartType_Magazine	UMETA(DisplayName = "Magazine"),
	PartType_Foregrip	UMETA(DisplayName = "Fore Grip"),
	PartType_Side		UMETA(DisplayName = "Side"),
	PartType_Sight		UMETA(DisplayName = "Sight"),
	PartType_Core		UMETA(DisplayName = "Core")
};

UCLASS(Blueprintable)
class CYBERWAR_API UPartMesh : public UObject
{
	GENERATED_BODY()

		UPROPERTY(EditDefaultsOnly)
		EPartType PartType;
	
	
};
