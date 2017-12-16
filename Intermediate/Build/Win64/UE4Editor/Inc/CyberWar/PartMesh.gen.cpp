// Copyright 1998-2017 Epic Games, Inc. All Rights Reserved.
/*===========================================================================
	Generated code exported from UnrealHeaderTool.
	DO NOT modify this manually! Edit the corresponding .h files instead!
===========================================================================*/

#include "GeneratedCppIncludes.h"
#include "Public/Weapon/PartMesh.h"
#ifdef _MSC_VER
#pragma warning (push)
#pragma warning (disable : 4883)
#endif
PRAGMA_DISABLE_DEPRECATION_WARNINGS
void EmptyLinkFunctionForGeneratedCodePartMesh() {}
// Cross Module References
	CYBERWAR_API UClass* Z_Construct_UClass_UPartMesh_NoRegister();
	CYBERWAR_API UClass* Z_Construct_UClass_UPartMesh();
	COREUOBJECT_API UClass* Z_Construct_UClass_UObject();
	UPackage* Z_Construct_UPackage__Script_CyberWar();
// End Cross Module References
	void UPartMesh::StaticRegisterNativesUPartMesh()
	{
	}
	UClass* Z_Construct_UClass_UPartMesh_NoRegister()
	{
		return UPartMesh::StaticClass();
	}
	UClass* Z_Construct_UClass_UPartMesh()
	{
		static UClass* OuterClass = nullptr;
		if (!OuterClass)
		{
			static UObject* (*const DependentSingletons[])() = {
				(UObject* (*)())Z_Construct_UClass_UObject,
				(UObject* (*)())Z_Construct_UPackage__Script_CyberWar,
			};
#if WITH_METADATA
			static const UE4CodeGen_Private::FMetaDataPairParam Class_MetaDataParams[] = {
				{ "IncludePath", "Weapon/PartMesh.h" },
				{ "ModuleRelativePath", "Public/Weapon/PartMesh.h" },
			};
#endif
			static const FCppClassTypeInfoStatic StaticCppClassTypeInfo = {
				TCppClassTypeTraits<UPartMesh>::IsAbstract,
			};
			static const UE4CodeGen_Private::FClassParams ClassParams = {
				&UPartMesh::StaticClass,
				DependentSingletons, ARRAY_COUNT(DependentSingletons),
				0x00100080u,
				nullptr, 0,
				nullptr, 0,
				nullptr,
				&StaticCppClassTypeInfo,
				nullptr, 0,
				METADATA_PARAMS(Class_MetaDataParams, ARRAY_COUNT(Class_MetaDataParams))
			};
			UE4CodeGen_Private::ConstructUClass(OuterClass, ClassParams);
		}
		return OuterClass;
	}
	IMPLEMENT_CLASS(UPartMesh, 2081732824);
	static FCompiledInDefer Z_CompiledInDefer_UClass_UPartMesh(Z_Construct_UClass_UPartMesh, &UPartMesh::StaticClass, TEXT("/Script/CyberWar"), TEXT("UPartMesh"), false, nullptr, nullptr, nullptr);
	DEFINE_VTABLE_PTR_HELPER_CTOR(UPartMesh);
PRAGMA_ENABLE_DEPRECATION_WARNINGS
#ifdef _MSC_VER
#pragma warning (pop)
#endif
